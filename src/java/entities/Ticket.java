/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Entity
@Table(name = "Ticket")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ticket.findAll", query = "SELECT t FROM Ticket t"),
    @NamedQuery(name = "Ticket.findById", query = "SELECT t FROM Ticket t WHERE t.id = :id"),
    @NamedQuery(name = "Ticket.findByPostedDate", query = "SELECT t FROM Ticket t WHERE t.postedDate = :postedDate"),
    @NamedQuery(name = "Ticket.findByTitle", query = "SELECT t FROM Ticket t WHERE t.title = :title"),
    @NamedQuery(name = "Ticket.findByType", query = "SELECT t FROM Ticket t WHERE t.type = :type"),
    @NamedQuery(name = "Ticket.findByState", query = "SELECT t FROM Ticket t WHERE t.state = :state"),
    @NamedQuery(name = "Ticket.findByRelevance", query = "SELECT t FROM Ticket t WHERE t.relevance = :relevance")})
public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PostedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "Title")
    private String title;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "Message")
    private String message;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 9)
    @Column(name = "Type")
    private String type;
    @Lob
    @Size(max = 65535)
    @Column(name = "AnnexInfo")
    private String annexInfo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "State")
    private boolean state;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Relevance")
    private int relevance;
    @JoinColumn(name = "UserFK", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private User userFK;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ticketFK")
    private Collection<Historical> historicalCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ticketFK")
    private Collection<Geolocation> geolocationCollection;

    public Ticket() {
    }

    public Ticket(Integer id) {
        this.id = id;
    }

    public Ticket(Integer id, Date postedDate, String title, String message, String type, boolean state, int relevance) {
        this.id = id;
        this.postedDate = postedDate;
        this.title = title;
        this.message = message;
        this.type = type;
        this.state = state;
        this.relevance = relevance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnnexInfo() {
        return annexInfo;
    }

    public void setAnnexInfo(String annexInfo) {
        this.annexInfo = annexInfo;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getRelevance() {
        return relevance;
    }

    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }

    public User getUserFK() {
        return userFK;
    }

    public void setUserFK(User userFK) {
        this.userFK = userFK;
    }

    @XmlTransient
    public Collection<Historical> getHistoricalCollection() {
        return historicalCollection;
    }

    public void setHistoricalCollection(Collection<Historical> historicalCollection) {
        this.historicalCollection = historicalCollection;
    }

    @XmlTransient
    public Collection<Geolocation> getGeolocationCollection() {
        return geolocationCollection;
    }

    public void setGeolocationCollection(Collection<Geolocation> geolocationCollection) {
        this.geolocationCollection = geolocationCollection;
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
        if (!(object instanceof Ticket)) {
            return false;
        }
        Ticket other = (Ticket) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Ticket[ id=" + id + " ]";
    }
    
}
