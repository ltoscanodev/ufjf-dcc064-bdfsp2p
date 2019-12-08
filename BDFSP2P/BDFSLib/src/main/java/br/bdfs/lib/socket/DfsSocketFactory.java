package br.bdfs.lib.socket;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.exceptions.ConnectionRefusedException;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.protocol.DfsAddress;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author ltosc
 */
public class DfsSocketFactory 
{
    private static SSLContext getSSLContext() throws DfsException
    {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(DfsSocketFactory.class.getResourceAsStream("/META-INF/bdfs.jks"), DfsConfig.SSL_SOCKET_KEY_PASSWORD.toCharArray());
            
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, DfsConfig.SSL_SOCKET_KEY_PASSWORD.toCharArray());
            
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
            
            return sslContext;
        } 
        catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException ex) 
        {
            throw new DfsException(String.format("Não foi possível inicializar o contexto SSL: %s", ex.getMessage()));
        }
    }
    
    private static ServerSocket createSSLServerSocket(DfsAddress address) throws DfsException, IOException
    {
        return getSSLContext().getServerSocketFactory().createServerSocket(address.getPort());
    }
    
    private static Socket createSSLSocket(DfsAddress address) throws DfsException
    {
        try 
        {
            return getSSLContext().getSocketFactory().createSocket(address.getIp(), address.getPort());
        }
        catch (IOException ex) 
        {
            throw new ConnectionRefusedException(String.format("Não foi possível se conectar ao endereço %s", address.toString()));
        }
    }
    
    private static ServerSocket createServerSocket(DfsAddress address) throws IOException
    {
        return new ServerSocket(address.getPort());
    }
    
    private static Socket createSocket(DfsAddress address) throws ConnectionRefusedException
    {
        try 
        {
            return new Socket(address.getIp(), address.getPort());
        } 
        catch (IOException ex)
        {
            throw new ConnectionRefusedException(String.format("Não foi possível se conectar ao endereço %s", address.toString()));
        }
    }
    
    public static ServerSocket newServerSocket(DfsAddress address, boolean useSSLSocket) throws DfsException, IOException
    {
        if(useSSLSocket)
        {
            return createSSLServerSocket(address);
        }
        else
        {
            return createServerSocket(address);
        }
    }
    
    public static Socket newSocket(DfsAddress address, boolean useSSLSocket) throws DfsException, IOException
    {
        if(useSSLSocket)
        {
            return createSSLSocket(address);
        }
        else
        {
            return createSocket(address);
        }
    }
}