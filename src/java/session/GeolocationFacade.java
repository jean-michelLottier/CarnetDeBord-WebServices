/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entities.Geolocation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import utils.CarnetDeBordUtils;

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

        Query query = em.createQuery("SELECT g"
                + " FROM Geolocation g"
                + " WHERE g.ticketFK.state = 1"
                + " AND g.Latitude BETWEEN :latMin AND :latMax"
                + " AND g.Longitude BETWEEN :lonMin AND :longMax"
                + " HAVING acos(sin(:latitude) * sin(g.Latitude) + cos(:latitude) * cos(g.Latitude) * cos(g.Longitude - (:longitude))) <= :r")
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
