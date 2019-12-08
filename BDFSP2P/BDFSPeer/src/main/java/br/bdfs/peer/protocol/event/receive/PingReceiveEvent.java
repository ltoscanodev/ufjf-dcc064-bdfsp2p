package br.bdfs.peer.protocol.event.receive;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.protocol.event.DfsReceiveEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class PingReceiveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "PING";

    @Override
    public DfsEventMessage receiveMessage(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        receivedEventMessage.getEventParamList().clear();
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        
        return receivedEventMessage;
    }
}
