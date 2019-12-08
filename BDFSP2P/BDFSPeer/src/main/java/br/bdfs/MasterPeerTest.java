package br.bdfs;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.context.DfsAppContext;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.peer.BDFSPeer;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class MasterPeerTest 
{
    public static void main(String[] args)
    {
        try
        {
            DfsConfig.setDebug(true);
            DfsConfig.setEnableLog(true);
            
            DfsAppContext.initialize();

            int addressPort = 6666;
            String storagePath = "D:\\BDFS\\peer_" + String.valueOf(addressPort);
            
            BDFSPeer peer = new BDFSPeer(addressPort, storagePath);
            peer.startPeer();
            
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
            Logger.getLogger(MasterPeerTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally 
        {
            DfsAppContext.close();
        }
    }
}
