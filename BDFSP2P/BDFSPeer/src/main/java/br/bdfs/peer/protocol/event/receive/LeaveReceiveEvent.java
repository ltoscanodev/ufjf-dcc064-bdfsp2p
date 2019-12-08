package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.BDFSPeerInfo;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class LeaveReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "LEAVE";
    
    private final BDFSPeerInfo currentPeer;
    
    public LeaveReceiveEvent(BDFSPeerInfo currentPeer)
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
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(key) || ObjectChecker.strIsNullOrEmpty(ip) || ObjectChecker.strIsNullOrEmpty(port))
        {
            throw new InvalidEventMessageException();
        }
        
        BDFSPeerInfo leavePeer = new BDFSPeerInfo(DfsAddress.fromString(ip, port));
        BDFSPeerInfo previousPeer = currentPeer.getPreviousPeer();
        BDFSPeerInfo nextPeer = currentPeer.getNextPeer();
        
        if (key.equalsIgnoreCase(previousPeer.getKey())) 
        {
            currentPeer.setPreviousPeer(leavePeer);
            receivedEventMessage.getEventParamList().put("STATUS", "OK");
        } 
        else if (key.equalsIgnoreCase(nextPeer.getKey())) 
        {
            currentPeer.setNextPeer(leavePeer);
            receivedEventMessage.getEventParamList().put("STATUS", "OK");
        } 
        else 
        {
            receivedEventMessage.getEventParamList().put("STATUS", "NONE");
        }

        return receivedEventMessage;
    }
}
