package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.lib.socket.DfsSocketConnection;
import br.bdfs.peer.model.DfsFile;
import br.bdfs.peer.model.controller.helper.DfsFileHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class ReadBufferReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "READBUFFER";
    
    private final String storagePath;
    private final DfsSocketConnection socketConnection;

    public ReadBufferReceiveEvent(String storagePath, DfsSocketConnection socketConnection) 
    {
        this.storagePath = storagePath + File.separator;
        this.socketConnection = socketConnection;
    }
    
    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        DfsLogger.logDebug("ReadBufferReceiveEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String filePath = receivedEventMessage.getEventParamList().get("FILE_PATH");
        String strFileOffset = receivedEventMessage.getEventParamList().get("FILE_OFFSET");
        String strFileLength = receivedEventMessage.getEventParamList().get("FILE_LENGTH");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(filePath) 
                || ObjectChecker.strIsNullOrEmpty(strFileOffset) || ObjectChecker.strIsNullOrEmpty(strFileLength))
        {
            throw new InvalidEventMessageException();
        }
        
        DfsFile file = DfsFileHelper.findUserFile(token, filePath);
        
        String localFilePath = String.format("%s%s", storagePath, file.getUuid());
        long dataOffset = Long.valueOf(strFileOffset);
        long dataLength = Long.valueOf(strFileLength);

        File localFile = new File(localFilePath);
        
        if (!localFile.exists()) 
        {
            throw new DfsException(String.format("Arquivo %s não existe", filePath));
        }
        
        long bytesToRead = (int) Math.min(localFile.length() - dataOffset, dataLength);
        receivedEventMessage.getEventParamList().put("FILE_LENGTH", String.valueOf(bytesToRead));
        socketConnection.writeString(receivedEventMessage.toString());
        
        DfsLogger.logDebug(String.format("Enviando dados para %s...", socketConnection.getAddress().toString()));
        
        byte[] buffer = new byte[DfsConfig.RW_BUFFER_LENGTH];
        long totalRead = 0;
        long read;

        try (FileInputStream fileInputStream = new FileInputStream(localFile)) 
        {
            fileInputStream.skip(dataOffset);
            
            do 
            {
                read = Math.min((bytesToRead - totalRead), DfsConfig.RW_BUFFER_LENGTH);
                fileInputStream.read(buffer, 0, Math.toIntExact(read));
                totalRead += read;

                socketConnection.writeBuffer(buffer, 0, Math.toIntExact(read));
            } 
            while (totalRead < bytesToRead);

            socketConnection.flushBuffer();
        }

        DfsLogger.logDebug(String.format("Dados enviados para %s", socketConnection.getAddress().toString()));
        
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        return receivedEventMessage;
    }
}
