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
public class ReadBufferSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "READBUFFER";
    
    private final String token;
    private final String remoteFilePath;
    private final byte[] bytesToRead;
    private final long offset;
    
    public ReadBufferSendEvent(String token, String remoteFilePath, byte[] bytesToRead, long offset)
    {
        this.token = token;
        this.remoteFilePath = remoteFilePath;
        this.bytesToRead = bytesToRead;
        this.offset = offset;
    }
    
    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException
    {
        DfsLogger.logDebug("WriteBufferSendEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        paramList.put("FILE_PATH", remoteFilePath);
        paramList.put("FILE_OFFSET", String.valueOf(offset));
        paramList.put("FILE_LENGTH", String.valueOf(bytesToRead.length));
        
        return sendBuffer(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), bytesToRead, MessageWithFile.ReceiveFile, waitForResponse);
    }
}
