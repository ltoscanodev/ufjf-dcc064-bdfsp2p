package br.bdfs.lib.protocol;

import br.bdfs.lib.protocol.event.dispatcher.notification.DfsEventNotification;
import br.bdfs.lib.protocol.event.dispatcher.notification.IDfsNotifyEvent;
import java.util.List;

/**
 *
 * @author ltosc
 */
public abstract class DfsProtocol implements IDfsNotifyEvent
{
    private final DfsEventNotification eventNotification;
    
    public DfsProtocol(List<String> eventList)
    {
        this.eventNotification = new DfsEventNotification();
        
        for(String eventName : eventList)
        {
            this.eventNotification.registerNotification(eventName, getDfsProtocolInstance());
        }
    }
    
    public final DfsProtocol getDfsProtocolInstance()
    {
        return this;
    }

    /**
     * @return the eventNotification
     */
    public final DfsEventNotification getEventNotification() 
    {
        return eventNotification;
    }
}
