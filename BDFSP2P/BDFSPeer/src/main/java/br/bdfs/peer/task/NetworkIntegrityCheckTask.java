package br.bdfs.peer.task;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.BDFSPeerInfo;
import br.bdfs.peer.protocol.event.send.JoinSendEvent;
import br.bdfs.peer.protocol.event.send.PingSendEvent;
import br.bdfs.peer.protocol.event.send.ProbeSendEvent;
import br.bdfs.peer.protocol.event.send.RoutingUpdateSendEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class NetworkIntegrityCheckTask 
{
    private final Timer taskTimer;
    private final BDFSPeerInfo currentPeer;
    
    public NetworkIntegrityCheckTask(BDFSPeerInfo currentPeer)
    {
        this.taskTimer = new Timer();
        this.currentPeer = currentPeer;
    }
    
    public void startTask()
    {
        taskTimer.schedule(new TimerTask()
        {
            @Override
            public void run() 
            {
                try 
                {
                    PingSendEvent pingEvent = new PingSendEvent(currentPeer.getAddress());
                    pingEvent.send(currentPeer.getNextPeer().getAddress(), true);
                    
                    DfsLogger.logDebug(String.format("A verificação da integridade da rede foi concluída com sucesso pelo nó %s", currentPeer.getAddress().toString()));
                } 
                catch (DfsException | IOException ex) 
                {
                    try 
                    {
                        DfsLogger.logInfo(String.format("Houve um erro na verificação de integridade da rede pelo nó %s", currentPeer.getAddress().toString()));
                        
                        DfsAddress nextAddress;
                        
                        if(currentPeer.getRoutingList().size() < 3)
                        {
                            nextAddress = currentPeer.getAddress();
                        }
                        else
                        {
                            nextAddress = currentPeer.getRoutingList().get(2).getAddress();
                        }
                        
                        JoinSendEvent joinEvent = new JoinSendEvent(currentPeer.getKey(), currentPeer.getAddress(), currentPeer.getNextPeer().getAddress());
                        DfsEventMessage responseEventMessage = joinEvent.send(nextAddress, true);
                        
                        String status = responseEventMessage.getEventParamList().get("STATUS");
                        
                        if(ObjectChecker.strIsNullOrEmpty(status) || !status.equalsIgnoreCase("OK"))
                        {
                            DfsLogger.logError(String.format("O nó %s não conseguiu restaurar a integridade da rede", currentPeer.getAddress().toString()));
                        }
                        
                        currentPeer.setNextPeer(new BDFSPeerInfo(nextAddress));
                        
                        ProbeSendEvent probeEvent = new ProbeSendEvent(currentPeer.getKey(), currentPeer.getAddress().toString());
                        responseEventMessage = probeEvent.send(currentPeer.getAddress(), true);

                        String network = responseEventMessage.getEventParamList().get("NETWORK");

                        if (ObjectChecker.strIsNullOrEmpty(network)) 
                        {
                            throw new InvalidEventMessageException();
                        }
                        
                        RoutingUpdateSendEvent routingEvent = new RoutingUpdateSendEvent(network);
                        routingEvent.send();
                        
                        DfsLogger.logInfo(String.format("O nó %s restaurou a integridade da rede", currentPeer.getAddress().toString()));
                    } 
                    catch (DfsException | IOException ex1) 
                    {
                        Logger.getLogger(NetworkIntegrityCheckTask.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    
                }
            }
        }, DfsConfig.NETWORK_INTEGRITY_CHECK_TASK_DELAY, DfsConfig.NETWORK_INTEGRITY_CHECK_TASK_PERIOD);
    }
    
    public void stopTask()
    {
        taskTimer.cancel();
    }
}
