package br.bdfs.client.event.send;

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
public class CreateSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "CREATE";
    
    private final String token;
    private final String remoteFilePath;
    
    public CreateSendEvent(String token, String remoteFilePath)
    {
        this.token = token;
        this.remoteFilePath = remoteFilePath;
    }
    
    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        DfsLogger.logDebug("CreateSendEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        paramList.put("FILE_PATH", remoteFilePath);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
