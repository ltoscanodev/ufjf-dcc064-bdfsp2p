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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class RoutingUpdateReceiveEvent extends DfsReceiveEvent 
{
    public static final String EVENT_NAME = "ROUTING";
    
    private final BDFSPeerInfo currentPeer;
    
    public RoutingUpdateReceiveEvent(BDFSPeerInfo currentPeer)
    {
        this.currentPeer = currentPeer;
    }
    
    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        DfsLogger.logDebug("RoutingUpdateReceiveEvent.receiveEvent()");
        
        String network = receivedEventMessage.getEventParamList().get("NETWORK");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(network))
        {
            throw new InvalidEventMessageException();
        }
        
        List<BDFSPeerInfo> networkNodeList = new ArrayList<>();
        String[] networkNodes = network.split(",");
        
        for(String networkNode : networkNodes)
        {
            networkNodeList.add(BDFSPeerInfo.fromString(networkNode));
        }
        
        Collections.sort(networkNodeList, new Comparator<BDFSPeerInfo>() 
        {
            @Override
            public int compare(BDFSPeerInfo firstNodeInfo, BDFSPeerInfo secondNodeInfo)
            {
                return DHTKey.compare(firstNodeInfo.getKey(), secondNodeInfo.getKey());
            }
        });
        
        int currentNodeIndex = networkNodeList.indexOf(currentPeer);
        List<BDFSPeerInfo> routingList = new ArrayList<>();
        routingList.add(currentPeer);
        
        for (int i = 1; i < networkNodeList.size(); i *= 2) 
        {
            int nodeIndex = (currentNodeIndex + i) % networkNodeList.size();
            routingList.add(networkNodeList.get(nodeIndex));
        }
        
        currentPeer.setRoutingList(routingList);
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        
        return receivedEventMessage;
    }
}
