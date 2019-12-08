package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.model.DfsFile;
import br.bdfs.peer.model.controller.helper.DfsFileHelper;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class GetAttributeReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "GETATTR";
    
    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        DfsLogger.logDebug("GetAttributeReceiveEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String filePath = receivedEventMessage.getEventParamList().get("FILE_PATH");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(filePath))
        {
            throw new InvalidEventMessageException();
        }
        
        DfsFile file = DfsFileHelper.findUserFile(token, filePath);
        receivedEventMessage.getEventParamList().put("FILE_LENGTH", String.valueOf(file.getSize()));
        
        return receivedEventMessage;
    }
}
