/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.ticket;

import com.carnetdebord.webservice.entities.Geolocation;
import com.carnetdebord.webservice.entities.Ticket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.carnetdebord.webservice.session.GeolocationFacadeLocal;
import com.carnetdebord.webservice.session.TicketFacadeLocal;
import com.carnetdebord.webservice.utils.CarnetDeBordUtils;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public class TicketService extends CarnetDeBordUtils implements ITicketService {

    public static final Logger logger = Logger.getLogger(TicketService.class.getName());

    @EJB
    private TicketFacadeLocal ticketFacade;
    @EJB
    private GeolocationFacadeLocal geolocationFacade;

    public TicketService() {
        try {
            this.ticketFacade = (TicketFacadeLocal) new InitialContext().lookup("java:module/TicketFacade");
            this.geolocationFacade = (GeolocationFacadeLocal) new InitialContext().lookup("java:module/GeolocationFacade");
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "Impossible to init TicketFacade object.", ex);
        }
    }

    /**
     *
     * @param ticketID
     * @return
     */
    @Override
    public Ticket findTicketByID(long ticketID) {
        if (ticketID < 0) {
            return null;
        }

        return ticketFacade.findTicketByID(ticketID);
    }

    /**
     *
     * @param userID
     * @param ticketID
     * @return
     */
    @Override
    public Ticket findUserTicket(long userID, long ticketID) {
        if (userID < 0 || ticketID < 0) {
            return null;
        }

        return ticketFacade.findTicketByID(userID, ticketID);
    }

    /**
     *
     * @param longitude
     * @param latitude
     * @param isAngularRadian
     * @param workingRadiuses
     * @return
     */
    @Override
    public List<Geolocation> getTicketsByGeolocation(double longitude, double latitude, boolean isAngularRadian, WorkingRadius... workingRadiuses) {
        WorkingRadius workingRadius = new WorkingRadius();
        if (workingRadiuses.length > 0) {
            workingRadius = workingRadiuses[0];
        }

        double radius = workingRadius.getRadType().equals(WorkingRadius.radiusType.RADIUS) ? workingRadius.getValue() : workingRadius.getValue() / 2;
        switch (workingRadius.getDistType()) {
            case CENTIMETER:
                radius /= KILOMETER_IN_CENTIMETER;
                break;
            case METER:
                radius /= KILOMETER_IN_METER;
                break;
            default:
                break;
        }
        List<Geolocation> geolocations = geolocationFacade.findGeolocationIntoWorkingRadius(latitude, longitude, radius, isAngularRadian);
        return geolocations;
    }
}
