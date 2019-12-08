package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.path.DfsPath;
import br.bdfs.lib.path.PathHelper;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.lib.socket.DfsSocketConnection;
import br.bdfs.peer.BDFSPeerInfo;
import br.bdfs.peer.model.DfsFile;
import br.bdfs.peer.model.controller.helper.DfsFileHelper;
import br.bdfs.peer.protocol.event.send.CpSendEvent;
import br.bdfs.peer.protocol.event.send.CpSendEvent.CopyMethod;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class CpReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "CP";
    
    private final BDFSPeerInfo currentPeer;
    private final String storagePath;
    private final DfsSocketConnection socketConnection;

    public CpReceiveEvent(BDFSPeerInfo currentPeer, DfsSocketConnection socketConnection) 
    {
        this.currentPeer = currentPeer;
        this.storagePath = currentPeer.getStoragePath() + File.separator;
        this.socketConnection = socketConnection;
    }

    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        DfsLogger.logDebug("CpEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String method = receivedEventMessage.getEventParamList().get("METHOD");
        
        if(ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(method))
        {
            throw new InvalidEventMessageException();
        }
        
        if(method.equalsIgnoreCase("LOCAL") || method.equalsIgnoreCase("REPLICA"))
        {
            String filePath = receivedEventMessage.getEventParamList().get("FILE_PATH");
            String strFileLength = receivedEventMessage.getEventParamList().get("FILE_LENGTH");
            receivedEventMessage.getEventParamList().clear();

            if (ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(filePath) || ObjectChecker.strIsNullOrEmpty(strFileLength))
            {
                throw new InvalidEventMessageException();
            }
            
            String fileName;
            String fileUUID;
            long fileSize;
            
            if(method.equalsIgnoreCase("LOCAL"))
            {
                fileName = PathHelper.getName(filePath);
                fileUUID = UUID.randomUUID().toString();
                fileSize = Long.valueOf(strFileLength);
            }
            else
            {
                DfsFile file = DfsFileHelper.findUserFile(token, filePath);
                
                fileName = file.getName();
                fileUUID = file.getUuid();
                fileSize = Long.valueOf(strFileLength);
            }

            DfsLogger.logDebug(String.format("Recebendo dados de %s...", socketConnection.getAddress().toString()));

            String localPath = String.format("%s%s", storagePath, fileUUID);
            byte[] buffer = new byte[DfsConfig.RW_BUFFER_LENGTH];
            long totalRead = 0;
            long read;

            File localFile = new File(localPath);

            try (FileOutputStream fileOutputStream = new FileOutputStream(localFile)) 
            {
                do
                {
                    read = Math.min((fileSize - totalRead), DfsConfig.RW_BUFFER_LENGTH);
                    socketConnection.readBuffer(buffer, 0, Math.toIntExact(read));
                    totalRead += read;

                    fileOutputStream.write(buffer, 0, Math.toIntExact(read));
                } 
                while (totalRead < fileSize);

                fileOutputStream.flush();
            }

            DfsLogger.logDebug(String.format("Dados recebidos de %s", socketConnection.getAddress().toString()));

            if(method.equalsIgnoreCase("LOCAL"))
            {
                try 
                {
                    DfsFileHelper.createFile(token, fileName, fileUUID, fileSize, new DfsPath(filePath));
                } 
                catch (DfsException ex) 
                {
                    localFile.delete();

                    receivedEventMessage.getEventParamList().put("STATUS", ex.getMessage());
                    return receivedEventMessage;
                }
                
                new Thread(new Runnable() 
                {
                    @Override
                    public void run() 
                    {
                        try 
                        {
                            CpSendEvent cpSendEvent = new CpSendEvent(token, filePath, localPath, CopyMethod.ReplicaCopy);
                            DfsEventMessage responseEventMessage = cpSendEvent.send(currentPeer.getPreviousPeer().getAddress(), true);

                            if (responseEventMessage.getEventParamList().containsKey("STATUS"))
                            {
                                String status = responseEventMessage.getEventParamList().get("STATUS");

                                if (!status.equalsIgnoreCase("OK")) 
                                {
                                    throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
                                }
                            }
                            
//                            cpSendEvent = new CpSendEvent(token, filePath, localPath, CopyMethod.ReplicaCopy);
//                            responseEventMessage = cpSendEvent.send(currentPeer.getNextPeer().getAddress(), true);
//
//                            if (responseEventMessage.getEventParamList().containsKey("STATUS"))
//                            {
//                                String status = responseEventMessage.getEventParamList().get("STATUS");
//
//                                if (!status.equalsIgnoreCase("OK")) 
//                                {
//                                    throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
//                                }
//                            }
                        }
                        catch (DfsException | IOException ex) 
                        {
                            Logger.getLogger(CpReceiveEvent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();
            }
            
//        ReedSolomonEncoder reedSolomonEncoder = new ReedSolomonEncoder(DfsConfig.REED_SOLOMON_DATA_SHARDS, DfsConfig.REED_SOLOMON_PARITY_SHARDS);
//        reedSolomonEncoder.encode(localFile);
        }
        else if(method.equalsIgnoreCase("REMOTE"))
        {
            String filePath = receivedEventMessage.getEventParamList().get("FILE_PATH");
            receivedEventMessage.getEventParamList().clear();

            if (ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(filePath))
            {
                throw new InvalidEventMessageException();
            }

            DfsFile file = DfsFileHelper.findUserFile(token, filePath);
            String localFilePath = String.format("%s%s", storagePath, file.getUuid());

            File readFile = new File(localFilePath);

            if (!readFile.exists()) 
            {
                throw new DfsException(String.format("Arquivo %s não existe", filePath));
            }

            receivedEventMessage.getEventParamList().put("FILE_LENGTH", String.valueOf(readFile.length()));
            socketConnection.writeString(receivedEventMessage.toString());

            byte[] buffer = new byte[DfsConfig.RW_BUFFER_LENGTH];
            long dataLength = readFile.length();
            long totalRead = 0;
            long read;

            DfsLogger.logDebug(String.format("Enviando dados para %s...", socketConnection.getAddress().toString()));

            try (FileInputStream fileInputStream = new FileInputStream(readFile)) 
            {
                do 
                {
                    read = Math.min((dataLength - totalRead), DfsConfig.RW_BUFFER_LENGTH);
                    fileInputStream.read(buffer, 0, Math.toIntExact(read));
                    totalRead += read;

                    socketConnection.writeBuffer(buffer, 0, Math.toIntExact(read));
                } 
                while (totalRead < dataLength);

                socketConnection.flushBuffer();
            }

            DfsLogger.logDebug(String.format("Dados enviados para %s", socketConnection.getAddress().toString()));
        }
        else
        {
            throw new InvalidEventMessageException();
        }
        
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        return receivedEventMessage;
    }
}
