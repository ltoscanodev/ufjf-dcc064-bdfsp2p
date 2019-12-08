package br.bdfs.peer.model.controller;

import br.bdfs.lib.context.DfsAppContext;
import br.bdfs.lib.exceptions.InvalidPathException;
import br.bdfs.lib.model.controller.AbstractJpaController;
import br.bdfs.lib.path.PathHelper;
import br.bdfs.peer.model.DfsDirectory;
import br.bdfs.peer.model.DfsFile;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ltosc
 */
@Repository
public class DfsFileJpaController extends AbstractJpaController<DfsFile>
{
    private static final DfsFileJpaController DFS_FILE_JPA_CONTROLLER = DfsAppContext.createInstance(DfsFileJpaController.class);
    
    public DfsFileJpaController()
    {
        super(DfsFile.class);
    }
    
    public static final DfsFileJpaController getInstance()
    {
        return DFS_FILE_JPA_CONTROLLER;
    }
    
    public DfsFile findUserFile(String token, String filePath) throws InvalidPathException
    {
        String fileName = PathHelper.getName(filePath);
        String directoryPath = PathHelper.previousPath(filePath);
        
        DfsDirectory userDirectory = DfsDirectoryJpaController.getInstance().findUserDirectory(token, directoryPath);
        
        String sql = "SELECT dfs_file.id, dfs_file.name, dfs_file.uuid, \n"
                + "		dfs_file.size, dfs_file.creation_time, dfs_file.directory\n"
                + "FROM dfs_file\n"
                + "INNER JOIN dfs_directory ON ((dfs_directory.id = dfs_file.directory) AND (dfs_directory.path = ?))\n"
                + "WHERE dfs_file.name = ?";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql, DfsFile.class);
        sqlQuery.setParameter(1, userDirectory.getPath());
        sqlQuery.setParameter(2, fileName);
        
        return (DfsFile)sqlQuery.getSingleResult();
    }
}
