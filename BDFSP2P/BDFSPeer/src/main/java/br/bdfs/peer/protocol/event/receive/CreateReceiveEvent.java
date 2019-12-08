package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.path.DfsPath;
import br.bdfs.lib.path.PathHelper;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.model.controller.helper.DfsFileHelper;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author ltosc
 */
public class CreateReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "CREATE";
    
    private final String storagePath;

    public CreateReceiveEvent(String storagePath) 
    {
        this.storagePath = storagePath + File.separator;
    }
    
    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        DfsLogger.logDebug("CreateReceiveEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String filePath = receivedEventMessage.getEventParamList().get("FILE_PATH");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(filePath))
        {
            throw new InvalidEventMessageException();
        }
        
        String fileName = PathHelper.getName(filePath);
        String fileUUID = UUID.randomUUID().toString();
        String localPath = String.format("%s%s", storagePath, fileUUID);
        
        new File(localPath).createNewFile();
        DfsFileHelper.createFile(token, fileName, fileUUID, 0L, new DfsPath(filePath));
        
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        return receivedEventMessage;
    }
}