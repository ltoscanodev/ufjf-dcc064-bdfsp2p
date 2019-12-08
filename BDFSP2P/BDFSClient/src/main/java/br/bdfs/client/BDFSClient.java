package br.bdfs.client;

import br.bdfs.client.event.send.CdSendEvent;
import br.bdfs.client.event.send.CpSendEvent;
import br.bdfs.client.event.send.CpSendEvent.CopyMethod;
import br.bdfs.client.event.send.CreateSendEvent;
import br.bdfs.client.event.send.GetAttributeSendEvent;
import br.bdfs.client.event.send.LoginSendEvent;
import br.bdfs.client.event.send.LogoutSendEvent;
import br.bdfs.client.event.send.LookupSendEvent;
import br.bdfs.client.event.send.LsSendEvent;
import br.bdfs.client.event.send.MkDirSendEvent;
import br.bdfs.client.event.send.ReadBufferSendEvent;
import br.bdfs.client.event.send.RmDirSendEvent;
import br.bdfs.client.event.send.RmSendEvent;
import br.bdfs.client.event.send.RmSendEvent.RmMethod;
import br.bdfs.client.event.send.SdSendEvent;
import br.bdfs.client.event.send.WriteBufferSendEvent;
import br.bdfs.lib.dht.DHTKey;
import br.bdfs.lib.exceptions.AuthException;
import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.exceptions.InvalidEventMessageException;
import br.bdfs.lib.misc.ObjectChecker;
import br.bdfs.lib.path.PathHelper;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.protocol.event.message.DfsEventMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class BDFSClient 
{
    private DfsAddress remoteAddress;
    
    private String userToken;
    private String homePath;
    private String currentPath;
    
    public BDFSClient(DfsAddress remoteAddress)
    {
        this.remoteAddress = remoteAddress;
        
        this.userToken = null;
        this.homePath = null;
        this.currentPath = null;
    }
    
    public DfsAddress lookup(String path) throws DfsException, IOException
    {
        String key = DHTKey.generate(path);
        LookupSendEvent lookupSendEvent = new LookupSendEvent(key);
        DfsEventMessage lookupResponseMessage;
        
        String forwardIp = remoteAddress.getStringIp();
        String forwardPort = remoteAddress.getStringPort();
        String status;
        
        do
        {
            lookupResponseMessage = lookupSendEvent.send(DfsAddress.fromString(forwardIp, forwardPort), true);
            status = lookupResponseMessage.getEventParamList().get("STATUS");
            
            if (ObjectChecker.strIsNullOrEmpty(status)) 
            {
                throw new InvalidEventMessageException();
            }
            
            forwardIp = lookupResponseMessage.getEventParamList().get("FORWARD_IP");
            forwardPort = lookupResponseMessage.getEventParamList().get("FORWARD_PORT");
        }
        while(status.equalsIgnoreCase("FORWARD"));
        
        String strIp = lookupResponseMessage.getEventParamList().get("IP");
        String strPort = lookupResponseMessage.getEventParamList().get("PORT");
        
        return DfsAddress.fromString(strIp, strPort);
    }
    
    public void login(String username, String password) throws DfsException, IOException
    {
        LoginSendEvent loginEvent = new LoginSendEvent(username, password);
        DfsEventMessage responseEventMessage = loginEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            throw new AuthException(responseEventMessage.getEventParamList().get("STATUS"));
        }
        
        userToken = responseEventMessage.getEventParamList().get("TOKEN");
        homePath = cd("~");
    }
    
    public void logout() throws DfsException, IOException
    {
        LogoutSendEvent logoutEvent = new LogoutSendEvent(userToken);
        logoutEvent.send(remoteAddress, false);
        userToken = null;
    }
    
    public void create(String remotePath) throws DfsException, IOException 
    {
        remoteAddress = lookup(PathHelper.previousPath(remotePath));
        CreateSendEvent createEvent = new CreateSendEvent(userToken, remotePath);
        DfsEventMessage responseEventMessage = createEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }
    
    public void cp(String remotePath, String localPath, CopyMethod copyMethod) throws DfsException, IOException 
    {
        remoteAddress = lookup(PathHelper.previousPath(remotePath));
        CpSendEvent cpEvent = new CpSendEvent(userToken, remotePath, localPath, copyMethod);
        DfsEventMessage responseEventMessage = cpEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }
    
    public long readBuffer(String remotePath, byte[] buffer, long offset) throws DfsException, IOException 
    {
        remoteAddress = lookup(PathHelper.previousPath(remotePath));
        ReadBufferSendEvent readEvent = new ReadBufferSendEvent(userToken, remotePath, buffer, offset);
        DfsEventMessage responseEventMessage = readEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
        
        return Long.valueOf(responseEventMessage.getEventParamList().get("FILE_LENGTH"));
    }
    
    public void writeBuffer(String remotePath, byte[] buffer) throws DfsException, IOException 
    {
        remoteAddress = lookup(PathHelper.previousPath(remotePath));
        WriteBufferSendEvent writeEvent = new WriteBufferSendEvent(userToken, remotePath, buffer);
        DfsEventMessage responseEventMessage = writeEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }
    
    public long getAttribute(String remotePath) throws DfsException, IOException 
    {
        remoteAddress = lookup(PathHelper.previousPath(remotePath));
        GetAttributeSendEvent getAttrEvent = new GetAttributeSendEvent(userToken, remotePath);
        DfsEventMessage responseEventMessage = getAttrEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
        
        return Long.valueOf(responseEventMessage.getEventParamList().get("FILE_LENGTH"));
    }
    
    public String pwd()
    {
        return currentPath;
    }
    
    public String cd(String path) throws DfsException, IOException
    {
        remoteAddress = lookup(path);
        CdSendEvent cdEvent = new CdSendEvent(userToken, path);
        DfsEventMessage responseEventMessage = cdEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
        }
        
        setCurrentPath(responseEventMessage.getEventParamList().get("PATH"));
        
        return currentPath;
    }
    
    public List<String> ls(String path) throws DfsException, IOException 
    {
        remoteAddress = lookup(path);
        LsSendEvent lsEvent = new LsSendEvent(userToken, path);
        DfsEventMessage responseEventMessage = lsEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
        }
        
        String childs = responseEventMessage.getEventParamList().get("DIR");
        String[] childSplit = childs.split(",");

        return Arrays.asList(childSplit);
    }

    public void mkdir(String path) throws DfsException, IOException 
    {
        remoteAddress = lookup(path);
        MkDirSendEvent mkDirEvent = new MkDirSendEvent(userToken, path);
        DfsEventMessage responseEventMessage = mkDirEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }
    
    public void rmdir(String path) throws DfsException, IOException 
    {
        remoteAddress = lookup(path);
        RmDirSendEvent rmDirEvent = new RmDirSendEvent(userToken, path);
        DfsEventMessage responseEventMessage = rmDirEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }

    public void rm(String path) throws DfsException, IOException 
    {
        remoteAddress = lookup(PathHelper.previousPath(path));
        RmSendEvent rmEvent = new RmSendEvent(userToken, path, RmMethod.LocalRm);
        DfsEventMessage responseEventMessage = rmEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }
    
    public List<String> sd(String... params) throws DfsException, IOException
    {
        if(params.length >= 2)
        {
            if (params[0].equalsIgnoreCase("-R") && params[1].equalsIgnoreCase(currentPath)) 
            {
                throw new DfsException("Não é possível remover o diretório atual");
            }
            
            remoteAddress = lookup(params[1]);
        }
        
        SdSendEvent sdEvent = new SdSendEvent(userToken, params);
        DfsEventMessage responseEventMessage = sdEvent.send(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
        else if(responseEventMessage.getEventParamList().containsKey("SHARED_DIR"))
        {
            String sharedDir = responseEventMessage.getEventParamList().get("SHARED_DIR");
            String[] sharedDirSplit = sharedDir.split(",");
            
            return Arrays.asList(sharedDirSplit);
        }
        
        return null;
    }

    /**
     * @return the remoteAddress
     */
    public DfsAddress getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * @param remoteAddress the remoteAddress to set
     */
    public void setRemoteAddress(DfsAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    /**
     * @return the userToken
     */
    public String getUserToken() {
        return userToken;
    }

    /**
     * @return the homePath
     */
    public String getHomePath() {
        return homePath;
    }

    /**
     * @param currentPath the currentPath to set
     */
    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }
}
