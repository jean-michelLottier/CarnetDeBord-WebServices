/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.ticket;

import com.carnetdebord.webservice.entities.Geolocation;
import com.carnetdebord.webservice.entities.Ticket;
import java.util.List;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public interface ITicketService {

    /**
     * <p>
     * Find a ticket with its geolocation by its <strong>id</strong>.</p>
     *
     * @param ticketID
     * @return Ticket otherwise null
     */
    public Ticket findTicketByID(long ticketID);

    /**
     * <p>
     * Find a user's ticket.</p>
     *
     * @param userID
     * @param ticketID
     * @return Ticket if it is found and this one belongs to the user. Otherwise
     * null.
     */
    public Ticket findUserTicket(long userID, long ticketID);

    /**
     * <p>
     * Find all tickets with their geolocation into the circle area defined by a
     * radius and a geographical position.</p>
     *
     * @param longitude
     * @param latitude
     * @param isAngularRadian specify if longitude and latitude are defined in
     * radian or no
     * @param workingRadiuses <strong>optionnal</strong> parameter : if you
     * don't specify a radius, a default one is used (radius of 2km).
     * @return
     */
    public List<Geolocation> getTicketsByGeolocation(double longitude, double latitude, boolean isAngularRadian, WorkingRadius... workingRadiuses);

    public void saveTicket(Ticket ticket);
}
