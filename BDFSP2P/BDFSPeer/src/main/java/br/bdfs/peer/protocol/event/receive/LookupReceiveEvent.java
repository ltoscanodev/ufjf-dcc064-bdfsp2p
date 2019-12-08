package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.BDFSPeerInfo;
import br.bdfs.lib.dht.DHTKey;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class LookupReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "LOOKUP";
    
    private final BDFSPeerInfo currentPeer;
    
    public LookupReceiveEvent(BDFSPeerInfo currentPeer)
    {
        this.currentPeer = currentPeer;
    }
    
    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        DfsLogger.logDebug("LookupReceiveEvent.receiveEvent()");
        
        String key = receivedEventMessage.getEventParamList().get("KEY");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(key))
        {
            throw new InvalidEventMessageException();
        }
        
        BDFSPeerInfo nextPeer = currentPeer.getNextPeer();
        
        if (DHTKey.between(key, currentPeer.getKey(), nextPeer.getKey())) 
        {
            receivedEventMessage.getEventParamList().put("STATUS", "OK");
            receivedEventMessage.getEventParamList().put("IP", currentPeer.getAddress().getStringIp());
            receivedEventMessage.getEventParamList().put("PORT", currentPeer.getAddress().getStringPort());
            
            return receivedEventMessage;
        }
        else if (currentPeer.getRoutingList().size() < 3)
        {
            receivedEventMessage.getEventParamList().put("STATUS", "FORWARD");
            receivedEventMessage.getEventParamList().put("FORWARD_IP", nextPeer.getAddress().getStringIp());
            receivedEventMessage.getEventParamList().put("FORWARD_PORT", nextPeer.getAddress().getStringPort());
            
            return receivedEventMessage;
        }
        else
        {
            String currentKey, nextKey;
            BDFSPeerInfo forwardPeer;
            
            for(int i = 0; i < (currentPeer.getRoutingList().size() - 1); i++)
            {
                currentKey = currentPeer.getRoutingList().get(i).getKey();
                nextKey = currentPeer.getRoutingList().get(i + 1).getKey();
                
                if(DHTKey.between(key, currentKey, nextKey))
                {
                    forwardPeer = currentPeer.getRoutingList().get(i);
                    
                    receivedEventMessage.getEventParamList().put("STATUS", "FORWARD");
                    receivedEventMessage.getEventParamList().put("FORWARD_IP", forwardPeer.getAddress().getStringIp());
                    receivedEventMessage.getEventParamList().put("FORWARD_PORT", forwardPeer.getAddress().getStringPort());
                    
                    return receivedEventMessage;
                }
            }
            
            forwardPeer = currentPeer.getRoutingList().get((currentPeer.getRoutingList().size() - 1));
            
            receivedEventMessage.getEventParamList().put("STATUS", "FORWARD");
            receivedEventMessage.getEventParamList().put("FORWARD_IP", forwardPeer.getAddress().getStringIp());
            receivedEventMessage.getEventParamList().put("FORWARD_PORT", forwardPeer.getAddress().getStringPort());
            
            return receivedEventMessage;
        }
    }
}
