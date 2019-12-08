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
public class LeaveSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "LEAVE";
    
    private final String key;
    private final String ip;
    private final String port;
    
    public LeaveSendEvent(String key, DfsAddress peerAddress)
    {
        this.key = key;
        this.ip = peerAddress.getStringIp();
        this.port = peerAddress.getStringPort();
    }

    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("KEY", key);
        paramList.put("IP", ip);
        paramList.put("PORT", port);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
