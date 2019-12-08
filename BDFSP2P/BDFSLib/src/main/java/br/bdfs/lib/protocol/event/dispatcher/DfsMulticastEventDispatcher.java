package br.bdfs.lib.protocol.event.dispatcher;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.multicast.DfsMulticastMessageReceiver;
import br.bdfs.lib.multicast.IDfsMulticastMessageNotification;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class DfsMulticastEventDispatcher implements Runnable
{
    private final DfsEventMessage receivedEventMessage;
    private final List<IDfsMulticastMessageNotification> notificationList;
    
    public DfsMulticastEventDispatcher(String receivedMsg, List<IDfsMulticastMessageNotification> notificationList) throws InvalidEventMessageException
    {
        this.receivedEventMessage = DfsEventMessage.fromString(receivedMsg);
        this.notificationList = notificationList;
    }

    @Override
    public void run() 
    {
        for (IDfsMulticastMessageNotification notify : notificationList) 
        {
            try 
            {
                notify.multicastMessageNotification(receivedEventMessage);
            }
            catch (DfsException ex)
            {
                Logger.getLogger(DfsMulticastMessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
