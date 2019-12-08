package br.bdfs.client.event.send;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.DfsSendEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class LookupSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "LOOKUP";
    
    private final String key;
    
    public LookupSendEvent(String key)
    {
        this.key = key;
    }

    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("key", key);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
