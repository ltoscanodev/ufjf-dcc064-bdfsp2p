package br.bdfs.peer;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.protocol.BDFSProtocol;
import br.bdfs.peer.protocol.event.send.JoinSendEvent;
import br.bdfs.peer.protocol.event.send.LeaveSendEvent;
import br.bdfs.peer.protocol.event.send.LookupSendEvent;
import br.bdfs.peer.protocol.event.send.ProbeSendEvent;
import br.bdfs.peer.protocol.event.send.ProbeSendEvent.ProbeType;
import br.bdfs.peer.protocol.event.send.RoutingUpdateSendEvent;
import br.bdfs.peer.task.NetworkIntegrityCheckTask;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class BDFSPeer 
{
    private final BDFSPeerInfo peerInfo;
    private final BDFSProtocol protocol;
    
    private final NetworkIntegrityCheckTask networkIntegrityCheckTask;
    
    public BDFSPeer(int addressPort, String storagePath) throws DfsException, IOException
    {
        this.peerInfo = new BDFSPeerInfo(addressPort, storagePath);
        this.protocol = new BDFSProtocol(peerInfo);
        
        this.networkIntegrityCheckTask = new NetworkIntegrityCheckTask(peerInfo);
    }
    
    public void startPeer() throws DfsException, IOException
    {
        protocol.startProtocol();
        networkIntegrityCheckTask.startTask();
    }
    
    public void stopPeer() throws DfsException, IOException
    {
        String network = probe(ProbeType.Network);
        network = network.replace(peerInfo.getAddress().toString() + ",", "");
        
        BDFSPeerInfo previousPeer = peerInfo.getPreviousPeer();
        BDFSPeerInfo nextPeer = peerInfo.getNextPeer();
        
        DfsLogger.logDebug("Saindo da rede BDFS...");
        
        LeaveSendEvent leaveSendEvent = new LeaveSendEvent(peerInfo.getKey(), previousPeer.getAddress());
        DfsEventMessage leaveResponseMessage = leaveSendEvent.send(nextPeer.getAddress(), true);
        
        String status = leaveResponseMessage.getEventParamList().get("STATUS");
        
        if(ObjectChecker.strIsNullOrEmpty(status) || !status.equalsIgnoreCase("OK"))
        {
            throw new DfsException("Ocorreu um erro ao sair da rede BDFS");
        }
        
        leaveSendEvent = new LeaveSendEvent(peerInfo.getKey(), nextPeer.getAddress());
        leaveResponseMessage = leaveSendEvent.send(previousPeer.getAddress(), true);
        
        status = leaveResponseMessage.getEventParamList().get("STATUS");
        
        if(ObjectChecker.strIsNullOrEmpty(status) || !status.equalsIgnoreCase("OK"))
        {
            throw new DfsException("Ocorreu um erro ao sair da rede BDFS");
        }
        
        RoutingUpdateSendEvent routingSendEvent = new RoutingUpdateSendEvent(network);
        routingSendEvent.send();
        
        DfsLogger.logDebug("O nó saiu da rede BDFS com sucesso");
        
        networkIntegrityCheckTask.stopTask();
        protocol.stopProtocol();
    }
    
    public void join(DfsAddress joinAddress) throws DfsException, IOException
    {
        JoinSendEvent joinSendEvent = new JoinSendEvent(peerInfo.getKey(), peerInfo.getAddress());
        DfsEventMessage joinResponseMessage;
        String status;
        
        String forwardIp = joinAddress.getStringIp();
        String forwardPort = joinAddress.getStringPort();
        
        do
        {
            joinResponseMessage = joinSendEvent.send(DfsAddress.fromString(forwardIp, forwardPort), true);
            status = joinResponseMessage.getEventParamList().get("STATUS");
            
            if (ObjectChecker.strIsNullOrEmpty(status)) 
            {
                throw new InvalidEventMessageException();
            }
            else if (status.equalsIgnoreCase("NONE")) 
            {
                throw new DfsException("Não foi possível adicionar o nó na rede");
            }
            
            forwardIp = joinResponseMessage.getEventParamList().get("FORWARD_IP");
            forwardPort = joinResponseMessage.getEventParamList().get("FORWARD_PORT");
        }
        while(!status.equalsIgnoreCase("OK"));
        
        String strPreviousIp = joinResponseMessage.getEventParamList().get("PREVIOUS_IP");
        String strPreviousPort = joinResponseMessage.getEventParamList().get("PREVIOUS_PORT");
        String strNextIp = joinResponseMessage.getEventParamList().get("NEXT_IP");
        String strNextPort = joinResponseMessage.getEventParamList().get("NEXT_PORT");

        if (ObjectChecker.strIsNullOrEmpty(strPreviousIp) || ObjectChecker.strIsNullOrEmpty(strNextIp))
        {
            throw new InvalidEventMessageException();
        }
        
        peerInfo.setPreviousPeer(new BDFSPeerInfo(DfsAddress.fromString(strPreviousIp, strPreviousPort)));
        peerInfo.setNextPeer(new BDFSPeerInfo(DfsAddress.fromString(strNextIp, strNextPort)));
        
        String network = probe(ProbeType.Network);
        RoutingUpdateSendEvent routingUpdateSendEvent = new RoutingUpdateSendEvent(network);
        routingUpdateSendEvent.send();
    }
    
    public String probe(ProbeType probeType) throws DfsException, IOException
    {
        if(probeType.equals(ProbeType.Jump))
        {
            ProbeSendEvent probeSendEvent = new ProbeSendEvent(peerInfo.getKey(), 0, String.valueOf(System.nanoTime()));
            probeSendEvent.send(peerInfo.getAddress(), false);
            return null;
        }
        else
        {
            ProbeSendEvent probeSendEvent = new ProbeSendEvent(peerInfo.getKey(), peerInfo.getAddress().toString());
            DfsEventMessage probeResponseMessage = probeSendEvent.send(peerInfo.getAddress(), true);
            
            String network = probeResponseMessage.getEventParamList().get("NETWORK");
            
            if(ObjectChecker.strIsNullOrEmpty(network))
            {
                throw new InvalidEventMessageException();
            }
            
            return network;
        }
    }
    
    public BDFSPeerInfo lookup(String key) throws DfsException, IOException
    {
        long startTime = System.nanoTime();
        
        LookupSendEvent lookupSendEvent = new LookupSendEvent(key);
        DfsEventMessage lookupResponseMessage;
        
        String forwardIp = peerInfo.getAddress().getStringIp();
        String forwardPort = peerInfo.getAddress().getStringPort();
        String status;
        int jumps = 0;
        
        do
        {
            lookupResponseMessage = lookupSendEvent.send(DfsAddress.fromString(forwardIp, forwardPort), true);
            status = lookupResponseMessage.getEventParamList().get("STATUS");
            
            if (ObjectChecker.strIsNullOrEmpty(status)) 
            {
                throw new InvalidEventMessageException();
            }
            
            forwardIp = lookupResponseMessage.getEventParamList().get("FORWARD_IP");
            forwardPort = lookupResponseMessage.getEventParamList().get("FORWARD_PORT");
            jumps++;
        }
        while(status.equalsIgnoreCase("FORWARD"));
        
        String strIp = lookupResponseMessage.getEventParamList().get("IP");
        String strPort = lookupResponseMessage.getEventParamList().get("PORT");
        
        DfsLogger.logDebug(String.format("LOOKUP resolvido após %s saltos em %s ms", jumps, ((System.nanoTime() - startTime) / 1000000)));
        
        return new BDFSPeerInfo(DfsAddress.fromString(strIp, strPort));
    }

    /**
     * @return the peerInfo
     */
    public BDFSPeerInfo getPeerInfo() {
        return peerInfo;
    }
}
