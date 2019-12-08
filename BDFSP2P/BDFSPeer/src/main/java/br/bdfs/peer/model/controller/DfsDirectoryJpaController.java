package br.bdfs.peer.model.controller;

import br.bdfs.lib.context.DfsAppContext;
import br.bdfs.lib.model.controller.AbstractJpaController;
import br.bdfs.peer.model.DfsDirectory;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ltosc
 */
@Repository
public class DfsDirectoryJpaController extends AbstractJpaController<DfsDirectory>
{
    private static final DfsDirectoryJpaController DFS_DIRECTORY_JPA_CONTROLLER = DfsAppContext.createInstance(DfsDirectoryJpaController.class);
    
    public DfsDirectoryJpaController()
    {
        super(DfsDirectory.class);
    }
    
    public static final DfsDirectoryJpaController getInstance()
    {
        return DFS_DIRECTORY_JPA_CONTROLLER;
    }
    
    public DfsDirectory findUserDirectory(String token, String path)
    {
        String sql = "SELECT DISTINCT dfs_directory.id, dfs_directory.name, dfs_directory.path,\n"
                + "         dfs_directory.creation_time, dfs_directory.parent_directory\n"
                + "FROM dfs_directory, \n"
                + "(\n"
                + "	SELECT dfs_user.id, dfs_directory.path \n"
                + "    FROM dfs_user \n"
                + "    INNER JOIN dfs_directory ON (dfs_directory.id = dfs_user.home_directory)\n"
                + "    WHERE dfs_user.token = ?\n"
                + ") AS home_dir\n"
                + "LEFT JOIN(\n"
                + "	SELECT dfs_user.id, dfs_directory.path \n"
                + "	FROM dfs_shared_directory \n"
                + "	INNER JOIN dfs_user ON (dfs_user.token = ? AND dfs_shared_directory.user = dfs_user.id)\n"
                + "	INNER JOIN dfs_directory ON (dfs_directory.id = dfs_shared_directory.directory)\n"
                + ") AS shared_dir ON shared_dir.id = home_dir.id\n"
                + "WHERE \n"
                + "	((dfs_directory.path LIKE home_dir.path OR dfs_directory.path LIKE concat(home_dir.path, '/%')) \n"
                + "    OR (dfs_directory.path LIKE shared_dir.path OR dfs_directory.path LIKE concat(shared_dir.path, '/%')))\n"
                + "    AND dfs_directory.path = ?";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql, DfsDirectory.class);
        sqlQuery.setParameter(1, token);
        sqlQuery.setParameter(2, token);
        sqlQuery.setParameter(3, path);
        
        return (DfsDirectory)sqlQuery.getSingleResult();
    }
    
    public List<DfsDirectory> getUserDirectories(String token, String parentPath)
    {
        String sql = "SELECT DISTINCT\n"
                + "	child_dir.id, child_dir.name, child_dir.path, \n"
                + "	child_dir.creation_time, child_dir.parent_directory \n"
                + "FROM \n"
                + "(\n"
                + "	SELECT * \n"
                + "    FROM dfs_directory \n"
                + "    WHERE dfs_directory.parent_directory = (SELECT id FROM dfs_directory WHERE dfs_directory.path = ?)) AS child_dir,\n"
                + "(\n"
                + "	SELECT dfs_user.id, dfs_directory.path \n"
                + "    FROM dfs_user \n"
                + "    INNER JOIN dfs_directory ON (dfs_directory.id = dfs_user.home_directory)\n"
                + "    WHERE dfs_user.token = ?\n"
                + ") AS home_dir\n"
                + "LEFT JOIN(\n"
                + "	SELECT dfs_user.id, dfs_directory.path \n"
                + "	FROM dfs_shared_directory \n"
                + "	INNER JOIN dfs_user ON (dfs_user.token = ? AND dfs_shared_directory.user = dfs_user.id)\n"
                + "	INNER JOIN dfs_directory ON (dfs_directory.id = dfs_shared_directory.directory)\n"
                + ") AS shared_dir ON shared_dir.id = home_dir.id\n"
                + "WHERE (child_dir.path LIKE concat(home_dir.path, '/%')) OR (child_dir.path LIKE concat(shared_dir.path, '/%'))";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql, DfsDirectory.class);
        sqlQuery.setParameter(1, parentPath);
        sqlQuery.setParameter(2, token);
        sqlQuery.setParameter(3, token);
        
        return sqlQuery.getResultList();
    }
    
    @Transactional
    public boolean deleteUserDirectory(String token, String path)
    {
        String sql = "DELETE FROM dfs_directory\n"
                + "WHERE dfs_directory.path IN\n"
                + "(\n"
                + "	SELECT * FROM\n"
                + "    (\n"
                + "		SELECT dfs_directory.path \n"
                + "        FROM dfs_directory,\n"
                + "        (\n"
                + "			SELECT dfs_user.id, dfs_directory.path \n"
                + "			FROM dfs_user \n"
                + "			INNER JOIN dfs_directory ON (dfs_directory.id = dfs_user.home_directory)\n"
                + "			WHERE dfs_user.token = ?\n"
                + "		) AS home_dir\n"
                + "        LEFT JOIN(\n"
                + "			SELECT dfs_user.id, dfs_directory.path \n"
                + "			FROM dfs_shared_directory \n"
                + "			INNER JOIN dfs_user ON (dfs_user.token = ? AND dfs_shared_directory.user = dfs_user.id)\n"
                + "			INNER JOIN dfs_directory ON (dfs_directory.id = dfs_shared_directory.directory)\n"
                + "		) AS shared_dir ON shared_dir.id = home_dir.id\n"
                + "		WHERE ((dfs_directory.path LIKE home_dir.path OR dfs_directory.path LIKE concat(home_dir.path, '/%')) \n"
                + "				OR (dfs_directory.path LIKE shared_dir.path OR dfs_directory.path LIKE concat(shared_dir.path, '/%')))\n"
                + "				AND dfs_directory.path = ?\n"
                + "	) AS del_dir\n"
                + ")\n"
                + "LIMIT 1";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql);
        sqlQuery.setParameter(1, token);
        sqlQuery.setParameter(2, token);
        sqlQuery.setParameter(3, path);
        
        return (sqlQuery.executeUpdate() > 0);
    }
}