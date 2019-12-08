package br.bdfs.lib;

import br.bdfs.lib.exceptions.InvalidPathException;
import br.bdfs.lib.path.PathHelper;
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
            String path = "/root/folder/file.txt";
            System.out.println(PathHelper.getFileDirectoryPath(path));
        } 
        catch (InvalidPathException ex)
        {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
