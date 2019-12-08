package br.bdfs.lib.multicast;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;

/**
 *
 * @author ltosc
 */
public interface IDfsMulticastMessageNotification 
{
    public void multicastMessageNotification(DfsEventMessage receivedEventMessage) throws DfsException;
}
