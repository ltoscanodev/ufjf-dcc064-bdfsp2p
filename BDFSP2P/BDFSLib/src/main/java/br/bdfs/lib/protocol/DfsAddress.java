package br.bdfs.lib.protocol;

import br.bdfs.lib.exceptions.DfsException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Objects;

/**
 *
 * @author ltosc
 */
public class DfsAddress {

    private final InetAddress ip;
    private final int port;
    private final String strAddress;
    
    public DfsAddress(int port) throws DfsException 
    {
        this.ip = getLocalAddress();
        this.port = port;
        this.strAddress = String.format("%s:%s", ip.getHostAddress(), port);
    }

    public DfsAddress(InetAddress ip, int port) 
    {
        this.ip = ip;
        this.port = port;
        this.strAddress = String.format("%s:%s", ip.getHostAddress(), port);
    }
    
    public static DfsAddress fromString(String address) throws DfsException, UnknownHostException
    {
        String[] addressSplit = address.split(":");
        
        if(addressSplit.length != 2)
        {
            throw new DfsException(String.format("A string %s não tem o formato válido para DfsAddress", address));
        }
        
        return fromString(addressSplit[0], addressSplit[1]);
    }
    
    public static DfsAddress fromString(String ip, String port) throws UnknownHostException 
    {
        return new DfsAddress(InetAddress.getByName(ip), Integer.valueOf(port.trim()));
    }

    public static DfsAddress fromString(String ip, int port) throws UnknownHostException 
    {
        return new DfsAddress(InetAddress.getByName(ip), port);
    }
    
    public static InetAddress getLocalAddress() throws DfsException
    {
        try
        {
            InetAddress candidateAddress = null;
            
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();)
            {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) 
                {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    
                    if (!inetAddr.isLoopbackAddress()) 
                    {
                        if (inetAddr.isSiteLocalAddress()) 
                        {
                            return inetAddr;
                        }
                        else if (candidateAddress == null) 
                        {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            
            if (candidateAddress != null) 
            {
                return candidateAddress;
            }
            
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            
            if (jdkSuppliedAddress == null) 
            {
                throw new DfsException("Não foi possível determinar o endereço LAN");
            }
            
            return jdkSuppliedAddress;
        } 
        catch (SocketException | UnknownHostException ex) 
        {
            throw new DfsException(String.format("Não foi possível determinar o endereço LAN: %s", ex.getMessage()));
        }
    }

    @Override
    public String toString() 
    {
        return strAddress;
    }

    @Override
    public boolean equals(Object other) 
    {
        if(other instanceof DfsAddress)
        {
            DfsAddress otherAddress = (DfsAddress)other;
            return strAddress.equalsIgnoreCase(otherAddress.toString());
        }
        
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.ip);
        hash = 43 * hash + this.port;
        hash = 43 * hash + Objects.hashCode(this.strAddress);
        return hash;
    }

    /**
     * @return the ip
     */
    public InetAddress getIp() 
    {
        return ip;
    }
    
    public String getStringIp() {
        return ip.getHostAddress();
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
    
    public String getStringPort()
    {
        return String.valueOf(port);
    }
}
