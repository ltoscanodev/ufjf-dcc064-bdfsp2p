/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.peer.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ltosc
 */
@Entity
@Table(name = "dfs_directory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DfsDirectory.findAll", query = "SELECT d FROM DfsDirectory d")
    , @NamedQuery(name = "DfsDirectory.findById", query = "SELECT d FROM DfsDirectory d WHERE d.id = :id")
    , @NamedQuery(name = "DfsDirectory.findByName", query = "SELECT d FROM DfsDirectory d WHERE d.name = :name")
    , @NamedQuery(name = "DfsDirectory.findByPath", query = "SELECT d FROM DfsDirectory d WHERE d.path = :path")
    , @NamedQuery(name = "DfsDirectory.findByCreationTime", query = "SELECT d FROM DfsDirectory d WHERE d.creationTime = :creationTime")})
public class DfsDirectory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "path")
    private String path;
    @Basic(optional = false)
    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    @JoinTable(name = "dfs_shared_directory", joinColumns = {
        @JoinColumn(name = "directory", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user", referencedColumnName = "id")})
    @ManyToMany
    private List<DfsUser> dfsUserList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dfsDirectory")
    private List<DfsFile> dfsFileList;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dfsDirectory")
//    private List<DfsUser> dfsUserList1;
    @OneToMany(mappedBy = "dfsDirectory")
    private List<DfsDirectory> dfsDirectoryList;
    @JoinColumn(name = "parent_directory", referencedColumnName = "id")
    @ManyToOne
    private DfsDirectory dfsDirectory;

    public DfsDirectory() {
    }

    public DfsDirectory(Integer id) {
        this.id = id;
    }

    public DfsDirectory(Integer id, String name, String path, Date creationTime) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.creationTime = creationTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    @XmlTransient
    public List<DfsUser> getDfsUserList() {
        return dfsUserList;
    }

    public void setDfsUserList(List<DfsUser> dfsUserList) {
        this.dfsUserList = dfsUserList;
    }

    @XmlTransient
    public List<DfsFile> getDfsFileList() {
        return dfsFileList;
    }

    public void setDfsFileList(List<DfsFile> dfsFileList) {
        this.dfsFileList = dfsFileList;
    }

//    @XmlTransient
//    public List<DfsUser> getDfsUserList1() {
//        return dfsUserList1;
//    }
//
//    public void setDfsUserList1(List<DfsUser> dfsUserList1) {
//        this.dfsUserList1 = dfsUserList1;
//    }

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
        if (!(object instanceof DfsDirectory)) {
            return false;
        }
        DfsDirectory other = (DfsDirectory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.bdfs.peer.model.DfsDirectory[ id=" + id + " ]";
    }
    
}
