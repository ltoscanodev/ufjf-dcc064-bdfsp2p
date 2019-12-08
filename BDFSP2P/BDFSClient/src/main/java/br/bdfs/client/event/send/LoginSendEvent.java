package br.bdfs.client.event.send;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.DfsSendEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class LoginSendEvent extends  DfsSendEvent
{
    public static final String EVENT_NAME = "LOGIN";
    
    private final String username;
    private final String password;
    
    public LoginSendEvent(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException
    {
        DfsLogger.logDebug("LoginEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("USERNAME", username);
        paramList.put("PASSWORD", password);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}