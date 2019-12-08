package br.bdfs.peer;

import br.bdfs.lib.exceptions.DfsException;
import br.bdfs.lib.protocol.DfsAddress;
import br.bdfs.lib.dht.DHTKey;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author ltosc
 */
public class BDFSPeerInfo
{
    private final DfsAddress address;
    private final String name;
    private final String key;
    
    private final String storagePath;
    
    private BDFSPeerInfo previousPeer;
    private BDFSPeerInfo nextPeer;
    
    private List<BDFSPeerInfo> routingList;
    
    public BDFSPeerInfo(int addressPort, String storagePath) throws DfsException
    {
        this.address = new DfsAddress(addressPort);
        this.name = address.toString();
        this.key = DHTKey.generate(name);
        
        this.storagePath = storagePath;
        
        this.previousPeer = this;
        this.nextPeer = this;
        
        this.routingList = null;
    }
    
    public BDFSPeerInfo(DfsAddress address)
    {
        this.address = address;
        this.name = address.toString();
        this.key = DHTKey.generate(name);
        
        this.storagePath = null;
        
        this.previousPeer = this;
        this.nextPeer = this;
        
        this.routingList = null;
    }
    
    public static BDFSPeerInfo fromString(String address) throws DfsException, UnknownHostException
    {
        return new BDFSPeerInfo(DfsAddress.fromString(address));
    }
    
    @Override
    public String toString()
    {
        return this.address.toString();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (obj instanceof BDFSPeerInfo) 
        {
            BDFSPeerInfo otherPeerInfo = (BDFSPeerInfo)obj;
            return this.key.equalsIgnoreCase(otherPeerInfo.getKey());
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.address);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.key);
        hash = 97 * hash + Objects.hashCode(this.storagePath);
        return hash;
    }
    
    /**
     * @return the address
     */
    public DfsAddress getAddress() {
        return address;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }
    
    /**
     * @return the storagePath
     */
    public String getStoragePath() {
        return storagePath;
    }

    /**
     * @return the previousPeer
     */
    public BDFSPeerInfo getPreviousPeer() {
        return previousPeer;
    }

    /**
     * @param previousPeer the previousPeer to set
     */
    public void setPreviousPeer(BDFSPeerInfo previousPeer) {
        this.previousPeer = previousPeer;
    }

    /**
     * @return the nextPeer
     */
    public BDFSPeerInfo getNextPeer() {
        return nextPeer;
    }

    /**
     * @param nextPeer the nextPeer to set
     */
    public void setNextPeer(BDFSPeerInfo nextPeer) {
        this.nextPeer = nextPeer;
    }

    /**
     * @return the routingList
     */
    public List<BDFSPeerInfo> getRoutingList() {
        return routingList;
    }

    /**
     * @param routingList the routingList to set
     */
    public void setRoutingList(List<BDFSPeerInfo> routingList) {
        this.routingList = routingList;
    }
}
