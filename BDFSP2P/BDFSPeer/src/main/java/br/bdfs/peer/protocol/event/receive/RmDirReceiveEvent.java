package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.dht.DHTKey;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.path.PathHelper;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.BDFSPeerInfo;
import br.bdfs.peer.model.DfsDirectory;
import br.bdfs.peer.model.DfsFile;
import br.bdfs.peer.model.controller.helper.DfsDirectoryHelper;
import br.bdfs.peer.protocol.event.send.LookupSendEvent;
import br.bdfs.peer.protocol.event.send.RmSendEvent;
import br.bdfs.peer.protocol.event.send.RmSendEvent.RmMethod;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class RmDirReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "RMDIR";
    
    private final BDFSPeerInfo currentPeer;
    
    public RmDirReceiveEvent(BDFSPeerInfo currentPeer) 
    {
        this.currentPeer = currentPeer;
    }
    
    public DfsAddress lookup(DfsAddress remoteAddress, String path) throws DfsException, IOException
    {
        String key = DHTKey.generate(path);
        LookupSendEvent lookupSendEvent = new LookupSendEvent(key);
        DfsEventMessage lookupResponseMessage;
        
        String forwardIp = remoteAddress.getStringIp();
        String forwardPort = remoteAddress.getStringPort();
        String status;
        
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
        }
        while(status.equalsIgnoreCase("FORWARD"));
        
        String strIp = lookupResponseMessage.getEventParamList().get("IP");
        String strPort = lookupResponseMessage.getEventParamList().get("PORT");
        
        return DfsAddress.fromString(strIp, strPort);
    }
    
    private void recursivelyRemoveSubDirectory(String token, DfsDirectory rootDirectory) throws DfsException, IOException
    {
        for(DfsDirectory subDirectory : rootDirectory.getDfsDirectoryList())
        {
            recursivelyRemoveSubDirectory(token, subDirectory);
        }
        
        for (DfsFile subDirFile : rootDirectory.getDfsFileList()) 
        {
            String remoteFilePath = PathHelper.concatPath(rootDirectory.getPath(), subDirFile.getName());
            DfsAddress remoteAddress = lookup(currentPeer.getAddress(), rootDirectory.getPath());

            RmSendEvent rmEvent = new RmSendEvent(token, remoteFilePath, RmMethod.LocalRm);
            DfsEventMessage responseEventMessage = rmEvent.send(remoteAddress, true);

            if (responseEventMessage.getEventParamList().containsKey("STATUS")) 
            {
                String status = responseEventMessage.getEventParamList().get("STATUS");

                if (!status.equalsIgnoreCase("OK")) 
                {
                    throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
                }
            }
        }
    }
    
    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        DfsLogger.logDebug("RmDirEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String path = receivedEventMessage.getEventParamList().get("DIR_PATH");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(path))
        {
            throw new InvalidEventMessageException();
        }
        
        recursivelyRemoveSubDirectory(token, DfsDirectoryHelper.findUserDirectory(token, path));
        
        if(DfsDirectoryHelper.deleteUserDirectory(token, path))
        {
            receivedEventMessage.getEventParamList().put("STATUS", "OK");
        }
        else
        {
            throw new DfsException("Caminho não encontrado");
        }
        
        return receivedEventMessage;
    }
}
