package br.bdfs.client.event.send;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.path.DfsPath;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.DfsSendEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class SdSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "SD";
    
    private final String token;
    private final String[] params;
    
    public SdSendEvent(String token, String[] params) 
    {
        this.token = token;
        this.params = params;
    }

    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException
    {
        DfsLogger.logDebug("SdEvent.sendEvent()");
        
        if(params.length == 0)
        {
            throw new DfsException("Parâmetros inválidos");
        }
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        
        switch (params[0]) 
        {
            case "-C":
            {
                if (params.length != 2) 
                {
                    throw new DfsException("Parâmetros inválidos");
                }
                
                paramList.put("METHOD", "CREATE");
                paramList.put("SHARED_DIR", new DfsPath(params[1]).toString());
                break;
            }
            case "-R":
            {
                if (params.length != 2) 
                {
                    throw new DfsException("Parâmetros inválidos");
                }
                
                paramList.put("METHOD", "REMOVE");
                paramList.put("SHARED_DIR", new DfsPath(params[1]).toString());
                break;
            }
            case "-S":
            {
                if (params.length != 3) 
                {
                    throw new DfsException("Parâmetros inválidos");
                }
                
                paramList.put("METHOD", "SHARE");
                paramList.put("SHARED_DIR", new DfsPath(params[1]).toString());
                paramList.put("SHARED_USERNAME", params[2]);
                
                break;
            }
            case "-L":
            {
                paramList.put("METHOD", "LIST");
                break;
            }
            default:
                throw new DfsException("Parâmetros inválidos");
        }
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
