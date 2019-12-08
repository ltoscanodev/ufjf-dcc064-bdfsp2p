package br.bdfs.peer.model.controller.helper;

import br.bdfs.lib.exceptions.AuthException;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.token.TokenHelper;
import br.bdfs.peer.model.DfsDirectory;
import br.bdfs.peer.model.DfsUser;
import br.bdfs.peer.model.controller.DfsUserJpaController;
import javax.persistence.NoResultException;

/**
 *
 * @author ltosc
 */
public class DfsUserHelper 
{
    public static DfsUser findByUserName(String username) throws AuthException
    {
        try
        {
            return DfsUserJpaController.getInstance().findByNamedQuerySingle("DfsUser.findByUsername", "username", username);
        }
        catch (NoResultException ex) 
        {
            throw new AuthException("Usuário não encontrado");
        }
    }
    
    public static DfsUser findUserByToken(String token) throws AuthException
    {
        try
        {
            return DfsUserJpaController.getInstance().findByNamedQuerySingle("DfsUser.findByToken", "token", token);
        }
        catch (NoResultException ex) 
        {
            throw new AuthException("Usuário não encontrado");
        }
    }
    
    public static boolean isLogged(String token)
    {
        try 
        {
            return (findUserByToken(token) != null);
        } 
        catch (AuthException ex) 
        {
            return false;
        }
    }
    
    public static DfsDirectory getUserHomeDirectory(String token) throws AuthException
    {
        try
        {
            DfsUser user = DfsUserJpaController.getInstance().findByNamedQuerySingle("DfsUser.findByToken", "token", token);
            return user.getDfsDirectory();
        }
        catch (NoResultException ex) 
        {
            throw new AuthException("Usuário não logado");
        }
    }
    
    public static String login(String username, String password) throws AuthException
    {
        try
        {
            DfsUserJpaController dfsUserJpaController = DfsUserJpaController.getInstance();
            DfsUser user = dfsUserJpaController.findByNamedQuerySingle("DfsUser.findByUsername", "username", username);
            
            if(!ObjectChecker.isNull(user.getToken()))
            {
                return user.getToken();
            }
            else
            {
                if (user.getPassword().equalsIgnoreCase(password)) 
                {
                    String userToken = TokenHelper.generate();
                    user.setToken(userToken);
                    dfsUserJpaController.edit(user);
                    
                    return userToken;
                } 
                else 
                {
                    throw new AuthException("Senha incorreta");
                }
            }
        }
        catch (NoResultException ex) 
        {
            throw new AuthException("Usuário não encontrado");
        }
    }
    
    public static void logout(String token) throws AuthException
    {
        try
        {
            DfsUserJpaController dfsUserJpaController = DfsUserJpaController.getInstance();
            
            DfsUser user = dfsUserJpaController.findByNamedQuerySingle("DfsUser.findByToken", "token", token);
            user.setToken(null);
            dfsUserJpaController.edit(user);
        }
        catch (NoResultException ex) 
        {
            throw new AuthException("Usuário não logado");
        }
    }
}
