/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entities.Geolocation;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Local
public interface GeolocationFacadeLocal {

    void create(Geolocation geolocation);

    void edit(Geolocation geolocation);

    void remove(Geolocation geolocation);

    Geolocation find(Object id);

    List<Geolocation> findAll();

    List<Geolocation> findRange(int[] range);

    int count();

    /**
     * <p>
     * Find tickets around the user.</p>
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @param isAngularRadian
     * @return
     */
    public List<Geolocation> findGeolocationIntoWorkingRadius(double latitude, double longitude, double radius, boolean isAngularRadian);

    public Geolocation findGeolocationByTicketID(long ticketID);
}
