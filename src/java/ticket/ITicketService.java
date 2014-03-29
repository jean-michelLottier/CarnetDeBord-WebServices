/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticket;

import entities.Geolocation;
import java.util.List;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public interface ITicketService {

    public List<Geolocation> getTicketsByGeolocation(double longitude, double latitude, WorkingRadius... workingRadiuses);
}
