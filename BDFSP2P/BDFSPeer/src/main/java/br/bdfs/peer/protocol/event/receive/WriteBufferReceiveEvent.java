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
import br.bdfs.peer.model.controller.DfsFileJpaController;
import br.bdfs.peer.model.controller.helper.DfsFileHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class WriteBufferReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "WRITEBUFFER";
    
    private final String storagePath;
    private final DfsSocketConnection socketConnection;

    public WriteBufferReceiveEvent(String storagePath, DfsSocketConnection socketConnection) 
    {
        this.storagePath = storagePath + File.separator;
        this.socketConnection = socketConnection;
    }

    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        DfsLogger.logDebug("WriteBufferReceiveEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String filePath = receivedEventMessage.getEventParamList().get("FILE_PATH");
        String strFileLength = receivedEventMessage.getEventParamList().get("FILE_LENGTH");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(filePath) || ObjectChecker.strIsNullOrEmpty(strFileLength))
        {
            throw new InvalidEventMessageException();
        }
        
        DfsFile file = DfsFileHelper.findUserFile(token, filePath);
        long fileSize = file.getSize();
        
        String localFilePath = String.format("%s%s", storagePath, file.getUuid());

        File localFile = new File(localFilePath);
        
        if (!localFile.exists()) 
        {
            throw new DfsException(String.format("Arquivo %s não existe", filePath));
        }
        
        DfsLogger.logDebug(String.format("Recebendo dados de %s...", socketConnection.getAddress().toString()));
        
        byte[] buffer = new byte[DfsConfig.RW_BUFFER_LENGTH];
        long appendFileSize = Long.valueOf(strFileLength);
        long totalRead = 0;
        long read;

        try (FileOutputStream fileOutputStream = new FileOutputStream(localFile, true)) 
        {
            do 
            {
                read = Math.min((appendFileSize - totalRead), DfsConfig.RW_BUFFER_LENGTH);
                socketConnection.readBuffer(buffer, 0, Math.toIntExact(read));
                totalRead += read;

                fileOutputStream.write(buffer, 0, Math.toIntExact(read));
            } while (totalRead < appendFileSize);

            fileOutputStream.flush();
        }

        DfsLogger.logDebug(String.format("Dados recebidos de %s", socketConnection.getAddress().toString()));
        
        file.setSize(fileSize + appendFileSize);
        DfsFileJpaController.getInstance().edit(file);
        
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        return receivedEventMessage;
    }
    
}
