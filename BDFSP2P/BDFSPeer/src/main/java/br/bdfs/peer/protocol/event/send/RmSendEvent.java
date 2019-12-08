package br.bdfs.peer.protocol.event.send;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.DfsSendEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class RmSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "RM";
    
    public enum RmMethod { LocalRm, ReplicaRm }
    
    private final String token;
    private final String path;
    private final RmMethod rmMethod;
    
    public RmSendEvent(String token, String path, RmMethod rmMethod) 
    {
        this.token = token;
        this.path = path;
        this.rmMethod = rmMethod;
    }

    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        DfsLogger.logDebug("RmEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        
        if (rmMethod.equals(RmMethod.LocalRm)) 
        {
            paramList.put("METHOD", "LOCAL");
        } 
        else 
        {
            paramList.put("METHOD", "REPLICA");
        }
        
        paramList.put("FILE_PATH", path);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
    
}