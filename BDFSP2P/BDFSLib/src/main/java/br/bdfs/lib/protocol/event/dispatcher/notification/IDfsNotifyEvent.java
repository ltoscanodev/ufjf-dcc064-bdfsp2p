package br.bdfs.lib.protocol.event.dispatcher.notification;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.socket.DfsSocketConnection;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;

/**
 *
 * @author ltosc
 */
public interface IDfsNotifyEvent 
{
    public void notifyEvent(DfsSocketConnection socketConnection, DfsEventMessage receivedEventMessage) throws DfsException;
}
