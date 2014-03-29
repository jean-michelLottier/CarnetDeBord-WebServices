/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticket;

import entities.Geolocation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public class TicketService implements ITicketService {

    @Override
    public List<Geolocation> getTicketsByGeolocation(double longitude, double latitude, WorkingRadius... workingRadiuses) {
        WorkingRadius workingRadius = new WorkingRadius();
        if (workingRadiuses.length > 0) {
            workingRadius = workingRadiuses[0];
        }

        List<Geolocation> geolocations = new ArrayList<>();

        //TODO
        return geolocations;
    }

}
