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
public class WriteBufferSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "WRITEBUFFER";
    
    private final String token;
    private final String remoteFilePath;
    private final byte[] bytesToWrite;
    
    public WriteBufferSendEvent(String token, String remoteFilePath, byte[] bytesToWrite)
    {
        this.token = token;
        this.remoteFilePath = remoteFilePath;
        this.bytesToWrite = bytesToWrite;
    }
    
    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException
    {
        DfsLogger.logDebug("WriteBufferSendEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        paramList.put("FILE_PATH", remoteFilePath);
        paramList.put("FILE_LENGTH", String.valueOf(bytesToWrite.length));
        
        return sendBuffer(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), bytesToWrite, MessageWithFile.SendFile, waitForResponse);
    }
}
