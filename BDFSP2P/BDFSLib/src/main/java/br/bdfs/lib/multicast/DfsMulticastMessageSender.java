package br.bdfs.lib.multicast;

import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class DfsMulticastMessageSender 
{
    private final DfsAddress multicastGroupAddress;
    
    public DfsMulticastMessageSender() throws UnknownHostException
    {
        this.multicastGroupAddress = DfsAddress.fromString("230.0.0.0", 4446);
    }
    
    public DfsMulticastMessageSender(DfsAddress multicastGroupAddress)
    {
        this.multicastGroupAddress = multicastGroupAddress;
    }
    
    public void send(DfsEventMessage eventMessage)
    {
        DatagramSocket udpSocket = null;
        
        try
        {
            udpSocket = new DatagramSocket();
            byte[] msgBuffer = eventMessage.toString().getBytes();
            
            DatagramPacket udpPacket = new DatagramPacket(msgBuffer, msgBuffer.length, multicastGroupAddress.getIp(), multicastGroupAddress.getPort());
            udpSocket.send(udpPacket);
        }
        catch (IOException ex) 
        {
            Logger.getLogger(DfsMulticastMessageSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            if(ObjectChecker.isNull(udpSocket))
            {
                udpSocket.close();
            }
        }
    }
}
