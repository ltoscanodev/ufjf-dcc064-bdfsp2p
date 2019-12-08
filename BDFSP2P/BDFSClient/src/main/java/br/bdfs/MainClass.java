package br.bdfs;

import br.bdfs.client.console.DfsClientConsole;
import br.bdfs.lib.config.DfsConfig;
import br.bdfs.lib.protocol.DfsAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class MainClass
{

    public static void main(String[] args)
    {
        try 
        {
            DfsConfig.setDebug(true);
            DfsConfig.setEnableLog(true);
            
            DfsClientConsole clientConsole = new DfsClientConsole(DfsAddress.fromString("localhost", 6666));
            clientConsole.startConsole();
        }
        catch (UnknownHostException ex) 
        {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
