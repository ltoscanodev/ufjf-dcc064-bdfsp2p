package br.bdfs;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.context.DfsAppContext;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.peer.BDFSPeer;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.util.SocketUtils;

/**
 *
 * @author ltosc
 */
public class PeerTest 
{
    public static void main(String[] args)
    {
        try
        {
            DfsConfig.setDebug(true);
            DfsConfig.setEnableLog(true);
            
            DfsAppContext.initialize();

            int addressPort = SocketUtils.findAvailableTcpPort(DfsConfig.SOCKET_MIN_PORT, DfsConfig.SOCKET_MAX_PORT);
            String storagePath = "D:\\BDFS\\peer_" + String.valueOf(addressPort);
            
            BDFSPeer peer = new BDFSPeer(addressPort, storagePath);
            peer.startPeer();
            peer.join(DfsAddress.fromString("192.168.1.74:6666"));

            Scanner scanner = new Scanner(System.in);
            String cmd;

            do 
            {
                cmd = scanner.nextLine();
            }
            while (!cmd.equalsIgnoreCase("EXIT"));

            peer.stopPeer();
        } 
        catch (DfsException | IOException ex) 
        {
            Logger.getLogger(PeerTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally 
        {
            DfsAppContext.close();
        }
    }
}
