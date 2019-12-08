package br.bdfs.peer.protocol.event.send;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.DfsSendEvent;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class JoinSendEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "JOIN";
    
    private final String key;
    private final String ip;
    private final String port;
    private final String forwardIp;
    private final String forwardPort;
    
    public JoinSendEvent(String key, DfsAddress nodeAddress)
    {
        this.key = key;
        this.ip = nodeAddress.getStringIp();
        this.port = nodeAddress.getStringPort();
        this.forwardIp = null;
        this.forwardPort = null;
    }
    
    public JoinSendEvent(String key, DfsAddress nodeAddress, DfsAddress forwardAddress)
    {
        this.key = key;
        this.ip = nodeAddress.getStringIp();
        this.port = nodeAddress.getStringPort();
        this.forwardIp = forwardAddress.getStringIp();
        this.forwardPort = forwardAddress.getStringPort();
    }
    
    @Override
    public DfsEventMessage send(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("KEY", key);
        paramList.put("IP", ip);
        paramList.put("PORT", port);
        
        if(!ObjectChecker.strIsNullOrEmpty(forwardIp) && !ObjectChecker.strIsNullOrEmpty(forwardPort))
        {
            paramList.put("FORWARD_IP", forwardIp);
            paramList.put("FORWARD_PORT", forwardPort);
        }
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
