package br.bdfs.lib.protocol.event.dispatcher.notification;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.socket.DfsSocketConnection;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class DfsEventNotification 
{
    private final HashMap<String, List<IDfsNotifyEvent>> notifyEventMap;
    private final List<String> lockNotificationList;
    
    public DfsEventNotification()
    {
        this.notifyEventMap = new HashMap<>();
        this.lockNotificationList = new ArrayList<>();
    }
    
    public void registerNotification(String eventName, IDfsNotifyEvent notify)
    {
        if(!notifyEventMap.containsKey(eventName))
        {
            List<IDfsNotifyEvent> notifyEventList = new ArrayList<>();
            notifyEventList.add(notify);
            
            notifyEventMap.put(eventName, notifyEventList);
        }
        else
        {
            notifyEventMap.get(eventName).add(notify);
        }
    }
    
    public void unregisterNotification(String eventName, IDfsNotifyEvent notify)
    {
        if(notifyEventMap.containsKey(eventName))
        {
            notifyEventMap.get(eventName).remove(notify);
        }
    }
    
    public boolean isLockedNotification(String eventName)
    {
        return lockNotificationList.contains(eventName);
    }
    
    public void lockNotification(String eventName)
    {
        if(notifyEventMap.containsKey(eventName) && !lockNotificationList.contains(eventName))
        {
            lockNotificationList.add(eventName);
        }
    }
    
    public void unlockNotification(String eventName)
    {
        if(lockNotificationList.contains(eventName))
        {
            lockNotificationList.remove(eventName);
        }
    }
    
    public void notifyEvent(DfsSocketConnection socketConnection, DfsEventMessage receivedEventMessage) throws DfsException
    {
        String eventName = receivedEventMessage.getEventName();
        
        if (notifyEventMap.containsKey(receivedEventMessage.getEventName())) 
        {
            if(lockNotificationList.contains(eventName))
            {
                DfsLogger.logDebug(String.format("O evento %s está bloqueado para notificação", eventName));
            }
            else
            {
                List<IDfsNotifyEvent> notifyEventList = notifyEventMap.get(receivedEventMessage.getEventName());

                for (IDfsNotifyEvent notify : notifyEventList) 
                {
                    notify.notifyEvent(socketConnection, receivedEventMessage);
                }
            }
        }
        else
        {
            DfsLogger.logDebug(String.format("O evento %s não está registrado para notificação", eventName));
        }
    }
}
