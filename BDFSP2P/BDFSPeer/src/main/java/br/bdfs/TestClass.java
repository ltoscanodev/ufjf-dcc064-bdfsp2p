package br.bdfs;

import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.context.DfsAppContext;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.peer.BDFSPeer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class TestClass
{
    public static void main(String[] args)
    {
        try 
        {
            DfsConfig.setDebug(true);
            DfsConfig.setEnableLog(true);
            
            DfsAppContext.initialize();
            
            String baseStoragePath = "D:\\BDFS";
            int addressPort = 6666;
            List<BDFSPeer> peerList = new ArrayList<>();
            
            BDFSPeer masterPeer = new BDFSPeer(addressPort, baseStoragePath + "\\peer_" + String.valueOf(addressPort));
            masterPeer.startPeer();
            
            peerList.add(masterPeer);
            
            for(int i = 1; i < 15; i++)
            {
                addressPort++;
                BDFSPeer slavePeer = new BDFSPeer(addressPort, baseStoragePath + "\\peer_" + String.valueOf(addressPort));
                slavePeer.startPeer();
                slavePeer.join(masterPeer.getPeerInfo().getAddress());
                
                peerList.add(slavePeer);
            }
            
            Scanner scanner = new Scanner(System.in);
            String cmd;
            
            do
            {
                cmd = scanner.nextLine();
            }
            while(!cmd.equalsIgnoreCase("EXIT"));
            
            for(BDFSPeer peer : peerList)
            {
                peer.stopPeer();
            }
        }
        catch (DfsException | IOException ex)
        {
            Logger.getLogger(TestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            DfsAppContext.close();
        }
    }
}
