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
public class LogoutSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "LOGOUT";
    
    private final String token;
    
    public LogoutSendEvent(String token)
    {
        this.token = token;
    }

    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        DfsLogger.logDebug("Logout.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
