package br.bdfs.client.event.send;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.ExistsException;
import br.bdfs.lib.exceptions.NotFoundException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.DfsSendEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class CpSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "CP";
    
    public enum CopyMethod { LocalCopy, RemoteCopy, ReplicaCopy }
    
    private final String token;
    private final String remoteFilePath;
    private final String localFilePath;
    private final CopyMethod copyMethod;
    
    public CpSendEvent(String token, String remoteFilePath, String localFilePath, CopyMethod copyMethod)
    {
        this.token = token;
        this.remoteFilePath = remoteFilePath;
        this.localFilePath = localFilePath;
        this.copyMethod = copyMethod;
    }

    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        DfsLogger.logDebug("CpEvent.sendEvent()");
        
        if(copyMethod.equals(CopyMethod.LocalCopy) || copyMethod.equals(CopyMethod.ReplicaCopy))
        {
            File sendFile = new File(localFilePath);

            if (!sendFile.exists()) {
                throw new NotFoundException(String.format("O arquivo %s não foi encontrado", localFilePath));
            }

            HashMap<String, String> paramList = new HashMap<>();
            paramList.put("TOKEN", token);
            
            if(copyMethod.equals(CopyMethod.LocalCopy))
            {
                paramList.put("METHOD", "LOCAL");
            }
            else
            {
                paramList.put("METHOD", "REPLICA");
            }
            
            paramList.put("FILE_PATH", remoteFilePath);
            paramList.put("FILE_LENGTH", String.valueOf(sendFile.length()));

            return sendFile(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), sendFile, MessageWithFile.SendFile, waitForResponse);
        }
        else
        {
            File receiveFile = new File(localFilePath);

            if (receiveFile.exists()) {
                throw new ExistsException(String.format("O arquivo %s já existe", localFilePath));
            }

            HashMap<String, String> paramList = new HashMap<>();
            paramList.put("TOKEN", token);
            paramList.put("METHOD", "REMOTE");
            paramList.put("FILE_PATH", remoteFilePath);

            return sendFile(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), receiveFile, MessageWithFile.ReceiveFile, waitForResponse);
        }
    }
}
