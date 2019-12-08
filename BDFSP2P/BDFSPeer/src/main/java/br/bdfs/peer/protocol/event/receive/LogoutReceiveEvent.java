package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.model.controller.helper.DfsUserHelper;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class LogoutReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "LOGOUT";

    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        DfsLogger.logDebug("LogoutEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(token))
        {
            throw new InvalidEventMessageException();
        }
        
        DfsUserHelper.logout(token);
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        
        return receivedEventMessage;
    }
}
