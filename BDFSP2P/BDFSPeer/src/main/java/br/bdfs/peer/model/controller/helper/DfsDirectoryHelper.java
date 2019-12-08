package br.bdfs.peer.model.controller.helper;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.ExistsException;
import br.bdfs.lib.exceptions.NotFoundException;
import br.bdfs.lib.path.DfsPath;
import br.bdfs.peer.model.DfsDirectory;
import br.bdfs.peer.model.controller.DfsDirectoryJpaController;
import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;

/**
 *
 * @author ltosc
 */
public class DfsDirectoryHelper 
{
    public static DfsDirectory findUserDirectory(String token, String path) throws DfsException
    {
        try
        {
            return DfsDirectoryJpaController.getInstance().findUserDirectory(token, path);
        }
        catch (NoResultException ex) 
        {
            throw new NotFoundException("Diretório não encontrado");
        }
    }
    
    public static DfsDirectory findDirectory(String path) throws DfsException
    {
        try
        {
            return DfsDirectoryJpaController.getInstance().findByNamedQuerySingle("DfsDirectory.findByPath", "path", path);
        }
        catch (NoResultException ex) 
        {
            throw new NotFoundException("Diretório não encontrado");
        }
    }
    
    public static boolean existsUserDirectory(String token, String dirPath)
    {
        try 
        {
            return (findUserDirectory(token, dirPath) != null);
        } 
        catch (DfsException ex) 
        {
            return false;
        }
    }
    
    public static boolean existsDirectory(String dirPath)
    {
        try 
        {
            return (findDirectory(dirPath) != null);
        } 
        catch (DfsException ex) 
        {
            return false;
        }
    }
    
    public static void createUserDirectory(String token, DfsPath dirPath) throws DfsException
    {
        if(existsUserDirectory(token, dirPath.toString()))
        {
            throw new ExistsException("O diretório já existe");
        }
        
        DfsDirectoryJpaController dfsDirectoryJpaController = DfsDirectoryJpaController.getInstance();
        
        DfsDirectory parentDir = null;
        DfsDirectory dir;
        
        while (dirPath.hasNext()) 
        {
            String path = dirPath.next();
            String name = dirPath.name();
            
            try 
            {
                parentDir = findUserDirectory(token, path);
            } 
            catch (NotFoundException ex) 
            {
                dir = new DfsDirectory();
                dir.setName(name);
                dir.setPath(path);
                dir.setCreationTime(new Date());
                dir.setDfsDirectory(parentDir);
                dfsDirectoryJpaController.create(dir);

                parentDir = dir;
            }
        }
    }
    
    public static List<DfsDirectory> getUserDirectories(String token, String parentPath) throws DfsException
    {
        return DfsDirectoryJpaController.getInstance().getUserDirectories(token, parentPath);
    }
    
    public static boolean deleteUserDirectory(String token, String path) throws DfsException
    {
        return DfsDirectoryJpaController.getInstance().deleteUserDirectory(token, path);
    }
}
