package br.bdfs.lib.server;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.ServerAlreadyRunningException;
import br.bdfs.lib.socket.DfsSocketFactory;
import br.bdfs.lib.log.DfsLogger;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.socket.DfsSocketConnection;
import br.bdfs.lib.protocol.event.dispatcher.DfsEventDispatcher;
import br.bdfs.lib.protocol.event.dispatcher.notification.DfsEventNotification;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ltosc
 */
public class DfsTcpServer implements Runnable
{
    private final DfsAddress serverAddress;
    private ServerSocket serverSocket;
    private boolean running;
    
    private final ThreadPoolExecutor clientThreadPool;
    private final DfsEventNotification eventNotification;
    
    public DfsTcpServer(int addressPort, DfsEventNotification eventNotification) throws DfsException
    {
        this.serverAddress = new DfsAddress(addressPort);
        this.serverSocket = null;
        this.running = false;
        
        this.clientThreadPool = new ThreadPoolExecutor(
                DfsConfig.THREADPOOL_MIN_THREAD, 
                DfsConfig.THREADPOOL_MAX_THREAD, 
                DfsConfig.THREADPOOL_KEEP_TO_ALIVE_THREAD, TimeUnit.SECONDS, 
                new LinkedBlockingQueue<>());
        
        this.eventNotification = eventNotification;
    }
    
    public DfsTcpServer(DfsAddress serverAddress, DfsEventNotification eventNotification) throws DfsException
    {
        this.serverAddress = serverAddress;
        this.serverSocket = null;
        this.running = false;
        
        this.clientThreadPool = new ThreadPoolExecutor(
                DfsConfig.THREADPOOL_MIN_THREAD, 
                DfsConfig.THREADPOOL_MAX_THREAD, 
                DfsConfig.THREADPOOL_KEEP_TO_ALIVE_THREAD, TimeUnit.SECONDS, 
                new SynchronousQueue());
        
        this.eventNotification = eventNotification;
    }
    
    public void startServer() throws ServerAlreadyRunningException
    {
        if(running)
        {
            throw new ServerAlreadyRunningException();
        }
        
        new Thread(this).start();
    }
    
    public void stopServer() throws IOException
    {
        if(running)
        {
            running = false;
            serverSocket.close();
            DfsLogger.logInfo("O servidor TCP parou");
        }
    }
    
    @Override
    public void run() 
    {
        try 
        {
            if(running) 
            {
                throw new ServerAlreadyRunningException();
            }
            
            serverSocket = DfsSocketFactory.newServerSocket(serverAddress, DfsConfig.USE_SSL_SOCKET);
            running = true;
            
            DfsLogger.logInfo(String.format("O servidor TCP foi iniciado no endereço %s", serverAddress.toString()));
            
            try
            {
                do
                {
                    Socket clientSocket = serverSocket.accept();
                    DfsSocketConnection socketConnection = new DfsSocketConnection(clientSocket);
                    
                    DfsLogger.logDebug(String.format("Nova conexão %s recebida", socketConnection.getAddress().toString()));
                    
                    clientThreadPool.execute(new DfsEventDispatcher(socketConnection, eventNotification));
                }
                while (running);
            }
            catch (SocketException ex)
            {
//                DfsLogger.logInfo("A espera por conexões terminou");
            }
        }
        catch (DfsException | IOException ex) 
        {
            DfsLogger.logError(String.format("Ocorreu um erro durante a execução do servidor TCP: %s", ex.getMessage()));
        }
    }

    /**
     * @return the serverAddress
     */
    public DfsAddress getServerAddress() {
        return serverAddress;
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }
}
