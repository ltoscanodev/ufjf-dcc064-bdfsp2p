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
public class LoginReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "LOGIN";
    
    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        DfsLogger.logDebug("LoginEvent.receiveEvent()");
        
        String username = receivedEventMessage.getEventParamList().get("USERNAME");
        String password = receivedEventMessage.getEventParamList().get("PASSWORD");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(username) || ObjectChecker.strIsNullOrEmpty(password))
        {
            throw new InvalidEventMessageException();
        }
        
        String token = DfsUserHelper.login(username, password);
        receivedEventMessage.getEventParamList().put("TOKEN", token);
        
        return receivedEventMessage;
    }
}