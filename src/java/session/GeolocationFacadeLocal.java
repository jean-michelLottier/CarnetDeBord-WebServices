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
    
}
