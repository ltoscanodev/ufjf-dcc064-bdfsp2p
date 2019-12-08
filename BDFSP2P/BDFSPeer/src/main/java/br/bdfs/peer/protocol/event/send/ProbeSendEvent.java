package br.bdfs.peer.protocol.event.send;

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
public class ProbeSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "PROBE";
    
    public enum ProbeType { Jump, Network }
    
    private final String key;
    private final String jumps;
    private final String timestamp;
    private final String network;
    
    public ProbeSendEvent(String key, int jumps, String timestamp)
    {
        this.key = key;
        this.jumps = String.valueOf(jumps);
        this.timestamp = timestamp;
        this.network = null;
    }
    
    public ProbeSendEvent(String key, String network)
    {
        this.key = key;
        this.jumps = null;
        this.timestamp = null;
        this.network = network;
    }
    
    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException
    {
        HashMap<String, String> paramList = new HashMap<>();
        
        if(waitForResponse)
        {
            paramList.put("KEY", key);
            paramList.put("NETWORK", network);
        }
        else
        {
            paramList.put("KEY", key);
            paramList.put("JUMPS", jumps);
            paramList.put("TIMESTAMP", timestamp);
        }
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}