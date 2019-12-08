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
public class CdSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "CD";
    
    private final String token;
    private final String path;
    
    public CdSendEvent(String token, String path) 
    {
        this.token = token;
        this.path = path;
    }
    
    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        DfsLogger.logDebug("CdEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        paramList.put("PATH", path);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
