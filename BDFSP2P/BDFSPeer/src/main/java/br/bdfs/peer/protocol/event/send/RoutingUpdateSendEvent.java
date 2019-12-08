package br.bdfs.peer.protocol.event.send;

import br.bdfs.lib.multicast.DfsMulticastMessageSender;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class RoutingUpdateSendEvent
{
    public static final String EVENT_NAME = "ROUTING";
    
    private final String network;
    
    public RoutingUpdateSendEvent(String network)
    {
        this.network = network;
    }
    
    public void send() throws UnknownHostException
    {
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("NETWORK", network);
        
        DfsMulticastMessageSender multicastSender = new DfsMulticastMessageSender();
        multicastSender.send(new DfsEventMessage(EVENT_NAME, paramList));
    }
}
