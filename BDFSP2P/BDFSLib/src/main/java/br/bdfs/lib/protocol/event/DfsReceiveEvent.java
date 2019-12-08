package br.bdfs.lib.protocol.event;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.socket.DfsSocketConnection;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public abstract class DfsReceiveEvent
{
    public abstract DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException;
    
    public void receiveEvent(DfsSocketConnection socketConnection, DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        long startTime = System.nanoTime();
        
        String strReceivedMsg = receivedEventMessage.toString();
        DfsLogger.logDebug(String.format("Mensagem recebida de %s: %s", socketConnection.getAddress().toString(), strReceivedMsg));
        
        String strWaitForResponse = receivedEventMessage.getEventParamList().get("WAIT_FOR_RESPONSE");
        
        if(ObjectChecker.strIsNullOrEmpty(strWaitForResponse))
        {
            throw new InvalidEventMessageException();
        }
        
        boolean waitForResponse = Boolean.valueOf(strWaitForResponse);
        
        if(waitForResponse)
        {
            receivedEventMessage = receiveMessage(receivedEventMessage);
            strReceivedMsg = receivedEventMessage.toString();
            
            socketConnection.writeString(strReceivedMsg);
            DfsLogger.logDebug(String.format("Resposta enviada para %s: %s", socketConnection.getAddress().toString(), strReceivedMsg));
        }
        else
        {
            receiveMessage(receivedEventMessage);
        }
        
        DfsLogger.logDebug(String.format("Tempo para processamento da mensagem recebida: %s ms", ((System.nanoTime() - startTime) / 1000000)));
    }
}