package br.bdfs.peer.protocol;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.multicast.DfsMulticastMessageReceiver;
import br.bdfs.lib.multicast.IDfsMulticastMessageNotification;
import br.bdfs.lib.protocol.DfsProtocol;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import br.bdfs.lib.server.DfsTcpServer;
import br.bdfs.lib.socket.DfsSocketConnection;
import br.bdfs.peer.BDFSPeerInfo;
import br.bdfs.peer.protocol.event.receive.CdReceiveEvent;
import br.bdfs.peer.protocol.event.receive.RmReceiveEvent;
import br.bdfs.peer.protocol.event.receive.JoinReceiveEvent;
import br.bdfs.peer.protocol.event.receive.LoginReceiveEvent;
import br.bdfs.peer.protocol.event.receive.LogoutReceiveEvent;
import br.bdfs.peer.protocol.event.receive.LookupReceiveEvent;
import br.bdfs.peer.protocol.event.receive.LsReceiveEvent;
import br.bdfs.peer.protocol.event.receive.MkDirReceiveEvent;
import br.bdfs.peer.protocol.event.receive.ProbeReceiveEvent;
import br.bdfs.peer.protocol.event.receive.CpReceiveEvent;
import br.bdfs.peer.protocol.event.receive.CreateReceiveEvent;
import br.bdfs.peer.protocol.event.receive.GetAttributeReceiveEvent;
import br.bdfs.peer.protocol.event.receive.LeaveReceiveEvent;
import br.bdfs.peer.protocol.event.receive.PingReceiveEvent;
import br.bdfs.peer.protocol.event.receive.ReadBufferReceiveEvent;
import br.bdfs.peer.protocol.event.receive.RmDirReceiveEvent;
import br.bdfs.peer.protocol.event.receive.RoutingUpdateReceiveEvent;
import br.bdfs.peer.protocol.event.receive.SdReceiveEvent;
import br.bdfs.peer.protocol.event.receive.WriteBufferReceiveEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class BDFSProtocol extends DfsProtocol implements IDfsMulticastMessageNotification
{
    private static final List<String> EVENT_LIST;
    
    static
    {
        EVENT_LIST = new ArrayList<>();
        
        EVENT_LIST.add(JoinReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(LeaveReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(ProbeReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(RoutingUpdateReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(LookupReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(PingReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(LoginReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(LogoutReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(CreateReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(CpReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(ReadBufferReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(WriteBufferReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(GetAttributeReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(CdReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(LsReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(MkDirReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(RmDirReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(RmReceiveEvent.EVENT_NAME);
        EVENT_LIST.add(SdReceiveEvent.EVENT_NAME);
    }
    
    private final BDFSPeerInfo peerInfo;
    
    private final DfsTcpServer tcpServer;
    private final DfsMulticastMessageReceiver multicastReceiver;

    public BDFSProtocol(BDFSPeerInfo peerInfo) throws DfsException, IOException 
    {
        super(EVENT_LIST);
        
        this.peerInfo = peerInfo;
        this.tcpServer = new DfsTcpServer(peerInfo.getAddress().getPort(), getEventNotification());
        
        this.multicastReceiver = new DfsMulticastMessageReceiver();
        this.multicastReceiver.registerNotification(getInstance());
        
        File storageDir = new File(peerInfo.getStoragePath());
        
        if(!storageDir.exists())
        {
            storageDir.mkdirs();
            DfsLogger.logInfo(String.format("Diretório de armazenamento %s criado", peerInfo.getStoragePath()));
        }
    }
    
    public final BDFSProtocol getInstance()
    {
        return this;
    }
    
    public void startProtocol() throws DfsException, IOException
    {
        tcpServer.startServer();
        multicastReceiver.startReceiver();
    }
    
    public void stopProtocol() throws IOException
    {
        tcpServer.stopServer();
        multicastReceiver.stopReceiver();
    }

    @Override
    public void notifyEvent(DfsSocketConnection socketConnection, DfsEventMessage receivedEventMessage) throws DfsException
    {
        try
        {
            switch (receivedEventMessage.getEventName())
            {
                case JoinReceiveEvent.EVENT_NAME: 
                {
                    JoinReceiveEvent joinReceiveEvent = new JoinReceiveEvent(peerInfo);
                    joinReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case LeaveReceiveEvent.EVENT_NAME: 
                {
                    LeaveReceiveEvent leaveReceiveEvent = new LeaveReceiveEvent(peerInfo);
                    leaveReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case ProbeReceiveEvent.EVENT_NAME: 
                {
                    ProbeReceiveEvent probeReceiveEvent = new ProbeReceiveEvent(peerInfo);
                    probeReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case LookupReceiveEvent.EVENT_NAME: 
                {
                    LookupReceiveEvent lookupReceiveEvent = new LookupReceiveEvent(peerInfo);
                    lookupReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case PingReceiveEvent.EVENT_NAME: 
                {
                    PingReceiveEvent pingReceiveEvent = new PingReceiveEvent();
                    pingReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case LoginReceiveEvent.EVENT_NAME:
                {
                    LoginReceiveEvent loginReceiveEvent = new LoginReceiveEvent();
                    loginReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case LogoutReceiveEvent.EVENT_NAME:
                {
                    LogoutReceiveEvent logoutReceiveEvent = new LogoutReceiveEvent();
                    logoutReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case CreateReceiveEvent.EVENT_NAME:
                {
                    CreateReceiveEvent createReceiveEvent = new CreateReceiveEvent(peerInfo.getStoragePath());
                    createReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case CpReceiveEvent.EVENT_NAME:
                {
                    CpReceiveEvent cpReceiveEvent = new CpReceiveEvent(peerInfo, socketConnection);
                    cpReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case ReadBufferReceiveEvent.EVENT_NAME:
                {
                    ReadBufferReceiveEvent readBufferReceiveEvent = new ReadBufferReceiveEvent(peerInfo.getStoragePath(), socketConnection);
                    readBufferReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case WriteBufferReceiveEvent.EVENT_NAME:
                {
                    WriteBufferReceiveEvent writeBufferReceiveEvent = new WriteBufferReceiveEvent(peerInfo.getStoragePath(), socketConnection);
                    writeBufferReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case GetAttributeReceiveEvent.EVENT_NAME:
                {
                    GetAttributeReceiveEvent getAttReceiveEvent = new GetAttributeReceiveEvent();
                    getAttReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case CdReceiveEvent.EVENT_NAME:
                {
                    CdReceiveEvent cdReceiveEvent = new CdReceiveEvent();
                    cdReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case LsReceiveEvent.EVENT_NAME:
                {
                    LsReceiveEvent lsReceiveEvent = new LsReceiveEvent();
                    lsReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case MkDirReceiveEvent.EVENT_NAME:
                {
                    MkDirReceiveEvent mkdirReceiveEvent = new MkDirReceiveEvent();
                    mkdirReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case RmDirReceiveEvent.EVENT_NAME:
                {
                    RmDirReceiveEvent rmdirReceiveEvent = new RmDirReceiveEvent(peerInfo);
                    rmdirReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case RmReceiveEvent.EVENT_NAME:
                {
                    RmReceiveEvent delReceiveEvent = new RmReceiveEvent(peerInfo);
                    delReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                case SdReceiveEvent.EVENT_NAME:
                {
                    SdReceiveEvent sdReceiveEvent = new SdReceiveEvent(peerInfo);
                    sdReceiveEvent.receiveEvent(socketConnection, receivedEventMessage);
                    break;
                }
                default:
                    DfsLogger.logError(String.format("Mensagem recebida não é aceita pelo protocolo BDFS", receivedEventMessage.toString()));
            }
        } 
        catch (IOException ex) 
        {
            throw new DfsException(ex.getMessage());
        }
    }

    @Override
    public void multicastMessageNotification(DfsEventMessage receivedEventMessage) throws DfsException 
    {
        try
        {
            DfsLogger.logDebug(receivedEventMessage.toString());
            
            switch (receivedEventMessage.getEventName())
            {
                case RoutingUpdateReceiveEvent.EVENT_NAME: 
                {
                    RoutingUpdateReceiveEvent routingUpdateReceiveEvent = new RoutingUpdateReceiveEvent(peerInfo);
                    receivedEventMessage = routingUpdateReceiveEvent.receiveMessage(receivedEventMessage);
                    DfsLogger.logDebug(receivedEventMessage.toString());
                    break;
                }
                default:
                    DfsLogger.logError(String.format("Mensagem recebida não é aceita pelo protocolo BDFS", receivedEventMessage.toString()));
            }
        } 
        catch (IOException ex) 
        {
            throw new DfsException(ex.getMessage());
        }
    }
}
