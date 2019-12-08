package br.bdfs.peer.model.controller.helper;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.ExistsException;
import br.bdfs.lib.exceptions.NotFoundException;
import br.bdfs.lib.path.DfsPath;
import br.bdfs.lib.path.PathHelper;
import br.bdfs.peer.model.DfsDirectory;
import br.bdfs.peer.model.DfsFile;
import br.bdfs.peer.model.controller.DfsFileJpaController;
import java.util.Date;
import javax.persistence.NoResultException;

/**
 *
 * @author ltosc
 */
public class DfsFileHelper 
{
    public static DfsFile findUserFile(String token, String path) throws DfsException
    {
        try
        {
            return DfsFileJpaController.getInstance().findUserFile(token, path);
        }
        catch (NoResultException ex) 
        {
            throw new NotFoundException(String.format("Arquivo %s não existe", path));
        }
    }
    
    public static boolean existsUserFile(String token, String filePath)
    {
        try 
        {
            return (findUserFile(token, filePath) != null);
        } 
        catch (DfsException ex) 
        {
            return false;
        }
    }
    
    public static void createFile(String token, String fileName, String fileUUID, long fileSize, DfsPath filePath) throws DfsException
    {
        if(existsUserFile(token, filePath.toString()))
        {
            throw new ExistsException(String.format("O arquivo %s já existe", filePath.toString()));
        }
        
        DfsFile file = new DfsFile();
        
        file.setName(fileName);
        file.setUuid(fileUUID);
        file.setSize(fileSize);
        file.setCreationTime(new Date());
        
        String directoryPath = PathHelper.previousPath(filePath.toString());
        DfsDirectory dfsDirectory = DfsDirectoryHelper.findUserDirectory(token, directoryPath);
        file.setDfsDirectory(dfsDirectory);
        
        DfsFileJpaController.getInstance().create(file);
    }
    
    public static void deleteUserFile(String token, String path) throws DfsException
    {
        DfsFile userFile = findUserFile(token, path);
        DfsFileJpaController.getInstance().remove(userFile.getId());
    }
}
