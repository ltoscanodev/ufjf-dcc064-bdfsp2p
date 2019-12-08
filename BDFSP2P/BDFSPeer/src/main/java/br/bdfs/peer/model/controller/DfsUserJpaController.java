package br.bdfs.peer.model.controller;

import br.bdfs.lib.context.DfsAppContext;
import br.bdfs.lib.model.controller.AbstractJpaController;
import br.bdfs.peer.model.DfsUser;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ltosc
 */
@Repository
public class DfsUserJpaController extends AbstractJpaController<DfsUser>
{
    private static final DfsUserJpaController DFS_USER_JPA_CONTROLLER = DfsAppContext.createInstance(DfsUserJpaController.class);
    
    public DfsUserJpaController()
    {
        super(DfsUser.class);
    }
    
    public static final DfsUserJpaController getInstance()
    {
        return DFS_USER_JPA_CONTROLLER;
    }
}