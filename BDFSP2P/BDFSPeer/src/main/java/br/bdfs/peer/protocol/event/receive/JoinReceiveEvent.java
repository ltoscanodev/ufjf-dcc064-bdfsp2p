package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.BDFSPeerInfo;
import br.bdfs.lib.dht.DHTKey;
import br.bdfs.peer.protocol.event.send.JoinSendEvent;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class JoinReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "JOIN";
    
    private final BDFSPeerInfo currentPeer;
    
    public JoinReceiveEvent(BDFSPeerInfo currentPeer)
    {
        this.currentPeer = currentPeer;
    }
    
    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        DfsLogger.logDebug("JoinReceiveEvent.receiveEvent()");
        
        String key = receivedEventMessage.getEventParamList().get("KEY");
        String ip = receivedEventMessage.getEventParamList().get("IP");
        String port = receivedEventMessage.getEventParamList().get("PORT");
        
        String forwardIp = receivedEventMessage.getEventParamList().get("FORWARD_IP");
        String forwardPort = receivedEventMessage.getEventParamList().get("FORWARD_PORT");
        
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(key) || ObjectChecker.strIsNullOrEmpty(ip) || ObjectChecker.strIsNullOrEmpty(port))
        {
            throw new InvalidEventMessageException();
        }
        
        BDFSPeerInfo joinPeer = new BDFSPeerInfo(DfsAddress.fromString(ip, port));
        BDFSPeerInfo previousPeer = currentPeer.getPreviousPeer();
        BDFSPeerInfo nextPeer = currentPeer.getNextPeer();
        
        if(!ObjectChecker.strIsNullOrEmpty(forwardIp) && !ObjectChecker.strIsNullOrEmpty(forwardPort))
        {
            DfsAddress forwardAddress = DfsAddress.fromString(forwardIp, forwardPort);
            
            if(forwardAddress.equals(previousPeer.getAddress()))
            {
                receivedEventMessage.getEventParamList().put("STATUS", "OK");
                currentPeer.setPreviousPeer(joinPeer);
            }
            else if(forwardAddress.equals(nextPeer.getAddress()))
            {
                receivedEventMessage.getEventParamList().put("STATUS", "OK");
                currentPeer.setNextPeer(joinPeer);
            }
            else
            {
                receivedEventMessage.getEventParamList().put("STATUS", "NONE");
            }
            
            return receivedEventMessage;
        }
        else
        {
            if (currentPeer.getKey().equals(nextPeer.getKey()))
            {
                receivedEventMessage.getEventParamList().put("STATUS", "OK");
                receivedEventMessage.getEventParamList().put("PREVIOUS_IP", currentPeer.getAddress().getStringIp());
                receivedEventMessage.getEventParamList().put("PREVIOUS_PORT", currentPeer.getAddress().getStringPort());
                receivedEventMessage.getEventParamList().put("NEXT_IP", currentPeer.getAddress().getStringIp());
                receivedEventMessage.getEventParamList().put("NEXT_PORT", currentPeer.getAddress().getStringPort());

                currentPeer.setPreviousPeer(joinPeer);
                currentPeer.setNextPeer(joinPeer);
                
                return receivedEventMessage;
            }
            else if (DHTKey.between(key, currentPeer.getKey(), nextPeer.getKey())) 
            {
                JoinSendEvent joinSendEvent = new JoinSendEvent(key, joinPeer.getAddress(), currentPeer.getAddress());
                DfsEventMessage joinResponseMessage = joinSendEvent.send(nextPeer.getAddress(), true);
                
                String status = joinResponseMessage.getEventParamList().get("STATUS");
                
                if(status.equalsIgnoreCase("OK"))
                {
                    receivedEventMessage.getEventParamList().put("STATUS", "OK");
                    receivedEventMessage.getEventParamList().put("PREVIOUS_IP", currentPeer.getAddress().getStringIp());
                    receivedEventMessage.getEventParamList().put("PREVIOUS_PORT", currentPeer.getAddress().getStringPort());
                    receivedEventMessage.getEventParamList().put("NEXT_IP", nextPeer.getAddress().getStringIp());
                    receivedEventMessage.getEventParamList().put("NEXT_PORT", nextPeer.getAddress().getStringPort());

                    currentPeer.setNextPeer(joinPeer);
                }
                else
                {
                    receivedEventMessage.getEventParamList().put("STATUS", "NONE");
                }
                
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

                for (int i = 0; i < (currentPeer.getRoutingList().size() - 1); i++) 
                {
                    currentKey = currentPeer.getRoutingList().get(i).getKey();
                    nextKey = currentPeer.getRoutingList().get(i + 1).getKey();

                    if (DHTKey.between(key, currentKey, nextKey))
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
}
