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
public class PingSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "PING";
    
    private final DfsAddress peerAddress;
    
    public PingSendEvent(DfsAddress peerAddress)
    {
        this.peerAddress = peerAddress;
    }

    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException
    {
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("ADDRESS", peerAddress.toString());
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
