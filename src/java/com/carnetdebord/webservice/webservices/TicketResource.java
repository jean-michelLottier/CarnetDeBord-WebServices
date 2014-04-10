/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.webservices;

import com.carnetdebord.webservice.entities.Geolocation;
import com.carnetdebord.webservice.entities.Ticket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import com.carnetdebord.webservice.ticket.ITicketService;
import com.carnetdebord.webservice.ticket.TicketService;
import com.carnetdebord.webservice.utils.CarnetDeBordUtils;

/**
 * REST Web Service
 *
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Path("ticket")
public class TicketResource extends CarnetDeBordUtils {

    private static final Logger logger = Logger.getLogger(TicketResource.class.getName());

    private static final String PATH_PARMAMETER_ID = "id";
    private static final String PATH_PARMAMETER_USER_ID = "userid";
    private static final String PATH_PARMAMETER_LONGITUDE = "longitude";
    private static final String PATH_PARMAMETER_LATITUDE = "latitude";

    @Context
    private UriInfo context;

    private ITicketService ticketService;

    public ITicketService getTicketService() {
        return ticketService;
    }

    public void setTicketService(ITicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Creates a new instance of TicketResource
     */
    public TicketResource() {
    }

    /**
     * Retrieves representation of an instance of webservices.TicketResource
     *
     *
     * @param userID
     * @param ticketID
     * @return an instance of java.lang.String
     */
    @Path("/{userid:(\\d+)}/id/{id: (\\d+)}")
    @GET
    @Produces("application/json")
    public Response getJson(@PathParam(PATH_PARMAMETER_USER_ID) long userID, @PathParam(PATH_PARMAMETER_ID) long ticketID) {
        logger.info("request GET");
        logger.log(Level.INFO, "userID : {0} ,id : {1}", new Object[]{userID, ticketID});

        Response.ResponseBuilder response = Response.ok();
        if (ticketID < 0 || userID < 0) {
            response.status(Response.Status.UNAUTHORIZED);
            return response.build();
        }

        ticketService = new TicketService();
        Ticket ticket = ticketService.findUserTicket(userID, ticketID);
        if (ticket == null) {
            response.status(Response.Status.UNAUTHORIZED);
            return response.build();
        }

        Json<Ticket> json = new Json<>();
        json.set(ticket);

        response.entity(json.generateJson());
        return response.build();
    }

    /**
     * Retrieves representation of an instance of webservices.TicketResource
     *
     * @param longitude
     * @param latitude
     * @return
     */
    @Path("/longitude/{longitude: [0-9.]+}/latitude/{latitude: [0-9.]+}")
    @GET
    @Produces("application/json")
    public Response getJson(@PathParam(PATH_PARMAMETER_LONGITUDE) double longitude,
            @PathParam(PATH_PARMAMETER_LATITUDE) double latitude) {
        logger.info("request GET");
        logger.log(Level.INFO, "longitude : {0}, latitude : {1}", new Object[]{longitude, latitude});

        Response.ResponseBuilder response = Response.ok();
        ticketService = new TicketService();
        List<Geolocation> geolocations = ticketService.getTicketsByGeolocation(longitude, latitude, true);
        if (geolocations == null || geolocations.isEmpty()) {
            response.status(Response.Status.NO_CONTENT);
            return response.build();
        }

        Json<List<Geolocation>> json = new Json<>();
        json.set(geolocations);

        response.entity(json.generateJson());
        return response.build();
    }

    /**
     * PUT method for updating or creating an instance of TicketResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
