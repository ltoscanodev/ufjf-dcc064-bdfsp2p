/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.peer.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ltosc
 */
@Entity
@Table(name = "dfs_user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DfsUser.findAll", query = "SELECT d FROM DfsUser d")
    , @NamedQuery(name = "DfsUser.findById", query = "SELECT d FROM DfsUser d WHERE d.id = :id")
    , @NamedQuery(name = "DfsUser.findByUsername", query = "SELECT d FROM DfsUser d WHERE d.username = :username")
    , @NamedQuery(name = "DfsUser.findByPassword", query = "SELECT d FROM DfsUser d WHERE d.password = :password")
    , @NamedQuery(name = "DfsUser.findByToken", query = "SELECT d FROM DfsUser d WHERE d.token = :token")})
public class DfsUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @Column(name = "password")
    private String password;
    @Column(name = "token")
    private String token;
    @ManyToMany(mappedBy = "dfsUserList")
    private List<DfsDirectory> dfsDirectoryList;
    @JoinColumn(name = "home_directory", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DfsDirectory dfsDirectory;

    public DfsUser() {
    }

    public DfsUser(Integer id) {
        this.id = id;
    }

    public DfsUser(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @XmlTransient
    public List<DfsDirectory> getDfsDirectoryList() {
        return dfsDirectoryList;
    }

    public void setDfsDirectoryList(List<DfsDirectory> dfsDirectoryList) {
        this.dfsDirectoryList = dfsDirectoryList;
    }

    public DfsDirectory getDfsDirectory() {
        return dfsDirectory;
    }

    public void setDfsDirectory(DfsDirectory dfsDirectory) {
        this.dfsDirectory = dfsDirectory;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DfsUser)) {
            return false;
        }
        DfsUser other = (DfsUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.bdfs.peer.model.DfsUser[ id=" + id + " ]";
    }
    
}
