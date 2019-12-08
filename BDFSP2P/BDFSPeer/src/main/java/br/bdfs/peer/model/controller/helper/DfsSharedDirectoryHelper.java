package br.bdfs.peer.model.controller.helper;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.ExistsException;
import br.bdfs.lib.path.DfsPath;
import br.bdfs.peer.model.DfsDirectory;
import br.bdfs.peer.model.DfsUser;
import br.bdfs.peer.model.controller.DfsDirectoryJpaController;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class DfsSharedDirectoryHelper 
{
    public static DfsDirectory findUserSharedDirectory(String token, DfsPath sharedDirPath) throws DfsException
    {
        return DfsDirectoryHelper.findUserDirectory(token, sharedDirPath.toString());
    }
    
    public static boolean existsUserSharedDirectory(String token, DfsPath sharedDirPath)
    {
        try 
        {
            return (DfsDirectoryHelper.findUserDirectory(token, sharedDirPath.toString()) != null);
        } 
        catch (DfsException ex) 
        {
            return false;
        }
    }
    
    public static void createSharedDirectory(String token, DfsPath dirPath) throws DfsException
    {
        if(dirPath.size() != 1)
        {
            throw new ExistsException("Apenas o diretório raiz deve ser usado para criar um diretório compartilhado");
        }
        
        if(DfsDirectoryHelper.existsDirectory(dirPath.toString()))
        {
            throw new ExistsException("O diretório compartilhado já existe");
        }
        
        DfsUser user = DfsUserHelper.findUserByToken(token);
        
        DfsDirectoryJpaController dfsDirectoryJpaController = DfsDirectoryJpaController.getInstance();
        DfsDirectory directory = new DfsDirectory();
        directory.setName(dirPath.name());
        directory.setPath(dirPath.current());
        directory.setCreationTime(new Date());
        directory.setDfsDirectory(null);
        dfsDirectoryJpaController.create(directory);
        
        List<DfsUser> sharedUserList = new ArrayList<>();
        sharedUserList.add(user);
        directory.setDfsUserList(sharedUserList);
        dfsDirectoryJpaController.edit(directory);
    }
    
    public static void removeSharedDirectory(String token, DfsPath sharedDirPath) throws DfsException
    {
        List<DfsDirectory> userSharedDirList = getUserSharedDirectories(token);
        String path = sharedDirPath.toString();
        
        for(DfsDirectory sharedDir : userSharedDirList)
        {
            if(sharedDir.getPath().equalsIgnoreCase(path))
            {
                DfsDirectoryJpaController dfsDirectoryJpaController = DfsDirectoryJpaController.getInstance();
                
                DfsDirectory directory = dfsDirectoryJpaController.findByNamedQuerySingle("DfsDirectory.findByPath", "path", path);
                dfsDirectoryJpaController.remove(directory.getId());
                return;
            }
        }
        
        throw new DfsException("Diretório compartilhado não encontrado");
    }
    
    public static void shareDirectoryWithUser(String token, String username, DfsPath sharedDirPath) throws DfsException
    {
        List<DfsDirectory> userSharedDirList = getUserSharedDirectories(token);
        DfsUser sharedUser = DfsUserHelper.findByUserName(username);
        
        for(DfsDirectory userSharedDir : userSharedDirList)
        {
            if(userSharedDir.getPath().equalsIgnoreCase(sharedDirPath.toString()))
            {
                userSharedDir.getDfsUserList().add(sharedUser);
                DfsDirectoryJpaController.getInstance().edit(userSharedDir);
                return;
            }
        }
        
        throw new DfsException("Diretório compartilhado não encontrado");
    }
    
    public static List<DfsDirectory> getUserSharedDirectories(String token) throws DfsException
    {
        DfsUser user = DfsUserHelper.findUserByToken(token);
        return user.getDfsDirectoryList();
    }
}
