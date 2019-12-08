/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.peer.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ltosc
 */
@Entity
@Table(name = "dfs_file")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DfsFile.findAll", query = "SELECT d FROM DfsFile d")
    , @NamedQuery(name = "DfsFile.findById", query = "SELECT d FROM DfsFile d WHERE d.id = :id")
    , @NamedQuery(name = "DfsFile.findByName", query = "SELECT d FROM DfsFile d WHERE d.name = :name")
    , @NamedQuery(name = "DfsFile.findByUuid", query = "SELECT d FROM DfsFile d WHERE d.uuid = :uuid")
    , @NamedQuery(name = "DfsFile.findBySize", query = "SELECT d FROM DfsFile d WHERE d.size = :size")
    , @NamedQuery(name = "DfsFile.findByCreationTime", query = "SELECT d FROM DfsFile d WHERE d.creationTime = :creationTime")})
public class DfsFile implements Serializable {

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
    @Column(name = "uuid")
    private String uuid;
    @Basic(optional = false)
    @Column(name = "size")
    private long size;
    @Basic(optional = false)
    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    @JoinColumn(name = "directory", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DfsDirectory dfsDirectory;

    public DfsFile() {
    }

    public DfsFile(Integer id) {
        this.id = id;
    }

    public DfsFile(Integer id, String name, String uuid, long size, Date creationTime) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.size = size;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
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
        if (!(object instanceof DfsFile)) {
            return false;
        }
        DfsFile other = (DfsFile) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.bdfs.peer.model.DfsFile[ id=" + id + " ]";
    }
    
}
