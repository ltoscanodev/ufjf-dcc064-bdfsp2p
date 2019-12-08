package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.path.DfsPath;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.model.controller.helper.DfsDirectoryHelper;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class MkDirReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "MKDIR";

    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        DfsLogger.logDebug("MkDirEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String path = receivedEventMessage.getEventParamList().get("PATH");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(path))
        {
            throw new InvalidEventMessageException();
        }
        
        DfsDirectoryHelper.createUserDirectory(token, new DfsPath(path));
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        
        return receivedEventMessage;
    }
}