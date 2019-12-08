package br.bdfs.lib.protocol.event.dispatcher;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.socket.DfsSocketConnection;
import br.bdfs.lib.protocol.event.dispatcher.notification.DfsEventNotification;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 *
 * @author ltosc
 */
public class DfsEventDispatcher implements Runnable
{
    private final DfsSocketConnection socketConnection;
    private final DfsEventNotification eventNotification;
    
    public DfsEventDispatcher(DfsSocketConnection socketConnection, DfsEventNotification eventNotification)
    {
        this.socketConnection = socketConnection;
        this.eventNotification = eventNotification;
    }
    
    @Override
    public void run() 
    {
        DfsEventMessage receivedEventMessage = null;
        
        try
        {
            socketConnection.setTimeout(DfsConfig.SOCKET_TIMEOUT);
            receivedEventMessage = DfsEventMessage.fromString(socketConnection.readString());
            eventNotification.notifyEvent(socketConnection, receivedEventMessage);
        }
        catch(SocketTimeoutException ex)
        {
            DfsLogger.logError(String.format("Nenhuma mensagem foi recebida de %s no tempo esperado", socketConnection.getAddress().toString()));
        }
        catch (DfsException | IOException ex1) 
        {
            DfsLogger.logError(String.format("Ocorreu um erro durante a notificação da mensagem de evento recebida: %s", ex1.getMessage()));
            
            if(!ObjectChecker.isNull(receivedEventMessage))
            {
                try 
                {
                    receivedEventMessage.getEventParamList().clear();
                    receivedEventMessage.getEventParamList().put("STATUS", ex1.getMessage());
                    socketConnection.writeString(receivedEventMessage.toString());
                } 
                catch (IOException ex2) 
                {
                    DfsLogger.logError(String.format("Não foi possível enviar mensagem de erro para %s", socketConnection.getAddress().toString()));
                }
            }
        }
        finally
        {
            try 
            {
                socketConnection.close();
                DfsLogger.logDebug(String.format("Conexão com %s encerrada", socketConnection.getAddress().toString()));
            } 
            catch (IOException ex) 
            {
                DfsLogger.logError(String.format("Ocorreu um erro ao fechar a conexão com %s: %s", socketConnection.getAddress().toString(), ex.getMessage()));
            }
        }
    }
}
