package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.path.DfsPath;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.peer.BDFSPeerInfo;
import br.bdfs.peer.model.DfsDirectory;
import br.bdfs.peer.model.controller.helper.DfsSharedDirectoryHelper;
import br.bdfs.peer.protocol.event.send.RmDirSendEvent;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class SdReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "SD";
    
    private BDFSPeerInfo currentPeer;
    
    public SdReceiveEvent(BDFSPeerInfo currentPeer)
    {
        this.currentPeer = currentPeer;
    }

    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        DfsLogger.logDebug("SdEvent.receiveEvent()");
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String method = receivedEventMessage.getEventParamList().get("METHOD");
        
        if(ObjectChecker.strIsNullOrEmpty(token) || ObjectChecker.strIsNullOrEmpty(method))
        {
            throw new InvalidEventMessageException();
        }
        
        switch(method.toUpperCase())
        {
            case "CREATE":
            {
                String sharedDir = receivedEventMessage.getEventParamList().get("SHARED_DIR");
                
                if (ObjectChecker.strIsNullOrEmpty(sharedDir))
                {
                    throw new InvalidEventMessageException();
                }
                
                DfsSharedDirectoryHelper.createSharedDirectory(token, new DfsPath(sharedDir));
                
                receivedEventMessage.getEventParamList().clear();
                receivedEventMessage.getEventParamList().put("STATUS", "OK");
                break;
            }
            case "REMOVE":
            {
                String sharedDir = receivedEventMessage.getEventParamList().get("SHARED_DIR");
                
                if (ObjectChecker.strIsNullOrEmpty(sharedDir))
                {
                    throw new InvalidEventMessageException();
                }
                
                if(DfsSharedDirectoryHelper.existsUserSharedDirectory(token, new DfsPath(sharedDir)))
                {
                    RmDirSendEvent rmDirEvent = new RmDirSendEvent(token, sharedDir);
                    DfsEventMessage responseEventMessage = rmDirEvent.send(currentPeer.getAddress(), true);
                    
                    if (responseEventMessage.getEventParamList().containsKey("STATUS"))
                    {
                        String status = responseEventMessage.getEventParamList().get("STATUS");

                        if (!status.equalsIgnoreCase("OK"))
                        {
                            throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
                        }
                    }
                    
                    receivedEventMessage.getEventParamList().clear();
                    receivedEventMessage.getEventParamList().put("STATUS", "OK");
                }
                else
                {
                    receivedEventMessage.getEventParamList().clear();
                    receivedEventMessage.getEventParamList().put("STATUS", "Diretório compartilhado não encontrado");
                }
                
                break;
            }
            case "SHARE":
            {
                String sharedDir = receivedEventMessage.getEventParamList().get("SHARED_DIR");
                String sharedUser = receivedEventMessage.getEventParamList().get("SHARED_USERNAME");
                
                if (ObjectChecker.strIsNullOrEmpty(sharedDir) || ObjectChecker.strIsNullOrEmpty(sharedUser))
                {
                    throw new InvalidEventMessageException();
                }
                
                DfsSharedDirectoryHelper.shareDirectoryWithUser(token, sharedUser, new DfsPath(sharedDir));
                
                receivedEventMessage.getEventParamList().clear();
                receivedEventMessage.getEventParamList().put("STATUS", "OK");
                break;
            }
            case "LIST":
            {
                List<DfsDirectory> sharedDirList = DfsSharedDirectoryHelper.getUserSharedDirectories(token);

                if (sharedDirList.isEmpty()) 
                {
                    receivedEventMessage.getEventParamList().clear();
                    receivedEventMessage.getEventParamList().put("STATUS", "Nenhum diretório compartilhado");
                } 
                else 
                {
                    StringBuilder sharedBuilder = new StringBuilder();

                    for (DfsDirectory dir : sharedDirList) {
                        sharedBuilder.append(dir.getPath());
                        sharedBuilder.append(",");
                    }

                    sharedBuilder.deleteCharAt(sharedBuilder.length() - 1);
                    
                    receivedEventMessage.getEventParamList().clear();
                    receivedEventMessage.getEventParamList().put("SHARED_DIR", sharedBuilder.toString());
                }
                break;
            }
            default:
                throw new InvalidEventMessageException();
        }
        
        return receivedEventMessage;
    }
}
