package br.bdfs.lib.multicast;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.exceptions.ServerAlreadyRunningException;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.dispatcher.DfsMulticastEventDispatcher;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ltosc
 */
public class DfsMulticastMessageReceiver implements Runnable
{
    private final DfsAddress multicastGroupAddress;
    private final MulticastSocket multicastSocket;
    private final Thread serverThread;
    private boolean running;
    
    private final ThreadPoolExecutor clientThreadPool;
    private final List<IDfsMulticastMessageNotification> notificationList;
    
    public DfsMulticastMessageReceiver() throws IOException
    {
        this.multicastGroupAddress = DfsAddress.fromString("230.0.0.0", 4446);
        this.multicastSocket = new MulticastSocket(multicastGroupAddress.getPort());
        this.serverThread = new Thread(this);
        this.running = false;
        
        this.clientThreadPool = new ThreadPoolExecutor(
                DfsConfig.THREADPOOL_MIN_THREAD, 
                DfsConfig.THREADPOOL_MAX_THREAD, 
                DfsConfig.THREADPOOL_KEEP_TO_ALIVE_THREAD, TimeUnit.SECONDS, 
                new SynchronousQueue());
        
        this.notificationList = new ArrayList<>();
    }
    
    public void registerNotification(IDfsMulticastMessageNotification notify)
    {
        if(!notificationList.contains(notify))
        {
            notificationList.add(notify);
        }
    }
    
    public void unregisterNotification(IDfsMulticastMessageNotification notify)
    {
        notificationList.remove(notify);
    }
    
    public void startReceiver() throws ServerAlreadyRunningException
    {
        if(running)
        {
            throw new ServerAlreadyRunningException();
        }
        
        serverThread.start();
    }
    
    public void stopReceiver() throws IOException
    {
        if(running)
        {
            running = false;
            multicastSocket.leaveGroup(multicastGroupAddress.getIp());
            multicastSocket.close();
            serverThread.stop();
            DfsLogger.logInfo("O servidor MULTICAST parou");
        }
    }
    
    @Override
    public void run() 
    {
        try
        {
            running = true;
            multicastSocket.joinGroup(multicastGroupAddress.getIp());
            
            DfsLogger.logInfo(String.format("O servidor MULTICAST foi iniciado no endereço %s", multicastGroupAddress.toString()));
            
            byte[] msgBuffer;
            DatagramPacket udpPacket;
            
            try
            {
                do
                {
                    try 
                    {
                        msgBuffer = new byte[65536];
                        udpPacket = new DatagramPacket(msgBuffer, msgBuffer.length);
                        multicastSocket.receive(udpPacket);
                        clientThreadPool.execute(new DfsMulticastEventDispatcher(new String(udpPacket.getData()), notificationList));
                    } 
                    catch (InvalidEventMessageException ex) 
                    {
                        DfsLogger.logError(ex.getMessage());
                    }
                } 
                while(running);
            }
            catch (SocketException ex)
            {
//                DfsLogger.logInfo("A espera por pacotes terminou");
            }
        }
        catch (IOException ex) 
        {
            DfsLogger.logError(String.format("Ocorreu um erro durante a execução do servidor multicast: %s", ex.getMessage()));
        }
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }
}
