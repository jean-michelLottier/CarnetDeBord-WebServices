/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.session;

import com.carnetdebord.webservice.entities.Geolocation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import com.carnetdebord.webservice.utils.CarnetDeBordUtils;
import org.hibernate.Session;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Stateless
public class GeolocationFacade extends AbstractFacade<Geolocation> implements GeolocationFacadeLocal {

    @PersistenceContext(unitName = "CarnetDeBordPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GeolocationFacade() {
        super(Geolocation.class);
    }

    @Override
    public List<Geolocation> findGeolocationIntoWorkingRadius(double latitude, double longitude, double radius, boolean isAngularRadian) {
        if (!isAngularRadian) {
            latitude = (latitude * Math.PI) / 180;
            longitude = (longitude * Math.PI) / 180;
        }

        double r = radius / CarnetDeBordUtils.EARTH_RADIUS;
        double latMin = latitude - r;
        double latMax = latitude + r;
        double deltaLong = Math.acos(Math.sin(r) / Math.cos(latitude));
        double longMin = longitude - deltaLong;
        double longMax = longitude + deltaLong;

        //acos(sin(:latitude) * sin(g.latitude) + cos(:latitude) * cos(g.latitude) * cos(g.longitude - (:longitude)))
        Query query = em.createQuery("SELECT g"
                + " FROM Geolocation g"
                + " WHERE g.ticketFK.state = 1"
                + " AND (g.latitude*3.141592653589793)/180 BETWEEN :latMin AND :latMax"
                + " AND (g.longitude*3.141592653589793)/180 BETWEEN :longMin AND :longMax"
                + " HAVING OPERATOR('Acos',OPERATOR('Sin',:latitude) * OPERATOR('Sin',(g.latitude*3.141592653589793)/180) + OPERATOR('Cos',:latitude) * OPERATOR('Cos',(g.latitude*3.141592653589793)/180) * OPERATOR('Cos',(g.longitude*3.141592653589793)/180 - (:longitude)))  <= :r")
                .setParameter("latitude", latitude)
                .setParameter("longitude", longitude)
                .setParameter("latMin", latMin)
                .setParameter("latMax", latMax)
                .setParameter("longMin", longMin)
                .setParameter("longMax", longMax)
                .setParameter("r", r);

        List<Geolocation> geolocations = (List<Geolocation>) query.getResultList();

        return geolocations;
    }

    @Override
    public Geolocation findGeolocationByTicketID(long ticketID) {
        if (ticketID < 0) {
            return null;
        }

        Query query = em.createQuery("SELECT g FROM Geolocation g WHERE g.ticketFK.id = :ticketID")
                .setParameter("ticketID", ticketID);

        Geolocation geolocation;
        try {
            geolocation = (Geolocation) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return geolocation;
    }

}
