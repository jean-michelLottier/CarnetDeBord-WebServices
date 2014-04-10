/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.carnetdebord.webservice.entities;

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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Entity
@Table(name = "Historical")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Historical.findAll", query = "SELECT h FROM Historical h"),
    @NamedQuery(name = "Historical.findById", query = "SELECT h FROM Historical h WHERE h.id = :id"),
    @NamedQuery(name = "Historical.findByFirstVisitedDate", query = "SELECT h FROM Historical h WHERE h.firstVisitedDate = :firstVisitedDate"),
    @NamedQuery(name = "Historical.findByLastVisitedDate", query = "SELECT h FROM Historical h WHERE h.lastVisitedDate = :lastVisitedDate")})
public class Historical implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FirstVisitedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstVisitedDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "LastVisitedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastVisitedDate;
    @JoinColumn(name = "UserFK", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private User userFK;
    @JoinColumn(name = "TicketFK", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Ticket ticketFK;

    public Historical() {
    }

    public Historical(Integer id) {
        this.id = id;
    }

    public Historical(Integer id, Date firstVisitedDate, Date lastVisitedDate) {
        this.id = id;
        this.firstVisitedDate = firstVisitedDate;
        this.lastVisitedDate = lastVisitedDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFirstVisitedDate() {
        return firstVisitedDate;
    }

    public void setFirstVisitedDate(Date firstVisitedDate) {
        this.firstVisitedDate = firstVisitedDate;
    }

    public Date getLastVisitedDate() {
        return lastVisitedDate;
    }

    public void setLastVisitedDate(Date lastVisitedDate) {
        this.lastVisitedDate = lastVisitedDate;
    }

    public User getUserFK() {
        return userFK;
    }

    public void setUserFK(User userFK) {
        this.userFK = userFK;
    }

    public Ticket getTicketFK() {
        return ticketFK;
    }

    public void setTicketFK(Ticket ticketFK) {
        this.ticketFK = ticketFK;
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
        if (!(object instanceof Historical)) {
            return false;
        }
        Historical other = (Historical) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Historical[ id=" + id + " ]";
    }
    
}
