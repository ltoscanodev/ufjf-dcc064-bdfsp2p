package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.BDFSPeerInfo;
import br.bdfs.peer.model.DfsFile;
import br.bdfs.peer.model.controller.helper.DfsFileHelper;
import br.bdfs.peer.protocol.event.send.RmSendEvent;
import br.bdfs.peer.protocol.event.send.RmSendEvent.RmMethod;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class RmReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "RM";
    
    private final BDFSPeerInfo currentPeer;
    private final String storagePath;
    
    public RmReceiveEvent(BDFSPeerInfo currentPeer) 
    {
        this.currentPeer = currentPeer;
        this.storagePath = currentPeer.getStoragePath() + File.separator;
    }

    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        DfsLogger.logDebug("RmEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String method = receivedEventMessage.getEventParamList().get("METHOD");
        String filePath = receivedEventMessage.getEventParamList().get("FILE_PATH");
        receivedEventMessage.getEventParamList().clear();
        
        if(ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(filePath))
        {
            throw new InvalidEventMessageException();
        }
        
        DfsFile file = DfsFileHelper.findUserFile(token, filePath);
        
        String localFilePath = String.format("%s%s", storagePath, file.getUuid());
        File localFile = new File(localFilePath);
        
        if(!localFile.exists())
        {
            throw new DfsException(String.format("O arquivo %s não foi encontrado", filePath));
        }
        
        if(method.equalsIgnoreCase("LOCAL"))
        {
            RmSendEvent rmEvent = new RmSendEvent(token, filePath, RmMethod.ReplicaRm);
            rmEvent.send(currentPeer.getPreviousPeer().getAddress(), true);
            
            if (localFile.delete())
            {
                DfsFileHelper.deleteUserFile(token, filePath);

                receivedEventMessage.getEventParamList().put("STATUS", "OK");
                return receivedEventMessage;
            } 
            else 
            {
                throw new DfsException(String.format("Não foi possível deletar o arquivo %s", filePath));
            }
        }
        else
        {
            if (localFile.delete()) 
            {
                receivedEventMessage.getEventParamList().put("STATUS", "OK");
                return receivedEventMessage;
            } 
            else 
            {
                throw new DfsException(String.format("Não foi possível deletar o arquivo %s", filePath));
            }
        }
    }
}
