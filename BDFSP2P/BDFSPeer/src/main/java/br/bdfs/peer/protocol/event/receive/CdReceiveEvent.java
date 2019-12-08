package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.model.DfsDirectory;
import br.bdfs.peer.model.controller.helper.DfsDirectoryHelper;
import br.bdfs.peer.model.controller.helper.DfsUserHelper;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class CdReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "CD";
    
    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        DfsLogger.logDebug("CdEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String path = receivedEventMessage.getEventParamList().get("PATH");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(path))
        {
            throw new InvalidEventMessageException();
        }
        
        DfsDirectory dir;

        if (path.equals("~")) 
        {
            dir = DfsUserHelper.getUserHomeDirectory(token);
        }
        else 
        {
            dir = DfsDirectoryHelper.findUserDirectory(token, path);
        }

        receivedEventMessage.getEventParamList().put("PATH", dir.getPath());
        return receivedEventMessage;
    }
}
