package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.BDFSPeerInfo;
import br.bdfs.peer.protocol.event.send.ProbeSendEvent;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class ProbeReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "PROBE";
    
    private final BDFSPeerInfo currentPeer;
    
    public ProbeReceiveEvent(BDFSPeerInfo currentPeer)
    {
        this.currentPeer = currentPeer;
    }
    
    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        DfsLogger.logDebug("ProbeReceiveEvent.receiveEvent()");
        
        String key = receivedEventMessage.getEventParamList().get("KEY");
        String strJumps = receivedEventMessage.getEventParamList().get("JUMPS");
        String timestamp = receivedEventMessage.getEventParamList().get("TIMESTAMP");
        String network = receivedEventMessage.getEventParamList().get("NETWORK");
        receivedEventMessage.getEventParamList().clear();
        
        BDFSPeerInfo nextPeer = currentPeer.getNextPeer();
        
        if(!ObjectChecker.strIsNullOrEmpty(key) && !ObjectChecker.strIsNullOrEmpty(strJumps) && !ObjectChecker.strIsNullOrEmpty(timestamp))
        {
            int jumps = Integer.valueOf(strJumps);

            if (key.equalsIgnoreCase(currentPeer.getKey()) && (jumps > 0)) 
            {
                long startTime = Long.valueOf(timestamp);
                long endTime = System.nanoTime();

                DfsLogger.logInfo(String.format("Mensagem de sonda retornada para %s após %s saltos em %s ms", 
                        currentPeer.getName(), 
                        jumps, 
                        ((endTime - startTime) / 1000000)));
            }
            else 
            {
                DfsLogger.logDebug(String.format("%s encaminhando mensagem de sonda para %s", currentPeer.getName(), nextPeer.getName()));

                ProbeSendEvent probeSendEvent = new ProbeSendEvent(key, (jumps + 1), timestamp);
                probeSendEvent.send(currentPeer.getNextPeer().getAddress(), false);
            }
        }
        else if(!ObjectChecker.strIsNullOrEmpty(network))
        {
            if(nextPeer.getKey().equalsIgnoreCase(key))
            {
                network = network.concat("," + currentPeer.toString());
                receivedEventMessage.getEventParamList().put("NETWORK", network);
                return receivedEventMessage;
            }
            else
            {
                if (!currentPeer.getKey().equals(key)) 
                {
                    network = network.concat("," + currentPeer.toString());
                }

                DfsLogger.logDebug(String.format("%s encaminhando mensagem de sonda para %s", currentPeer.getName(), nextPeer.getName()));
                
                ProbeSendEvent probeSendEvent = new ProbeSendEvent(key, network);
                return probeSendEvent.send(nextPeer.getAddress(), true);
            }
        }
        else
        {
            throw new InvalidEventMessageException();
        }
        
        return null;
    }
}
