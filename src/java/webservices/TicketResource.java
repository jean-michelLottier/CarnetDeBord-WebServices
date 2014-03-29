/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices;

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

/**
 * REST Web Service
 * 
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Path("ticket")
public class TicketResource {

    private static final Logger logger = Logger.getLogger(TicketResource.class.getName());

    private static final String PATH_PARMAMETER_ID = "id";
    private static final String PATH_PARMAMETER_LONGITUDE = "longitude";
    private static final String PATH_PARMAMETER_LATITUDE = "latitude";

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of TicketResource
     */
    public TicketResource() {
    }

    /**
     * Retrieves representation of an instance of webservices.TicketResource
     * 
     *
     * @param id
     * @return an instance of java.lang.String
     */
    @Path("/id/{id: (\\d+)}")
    @GET
    @Produces("application/json")
    public String getJson(@PathParam(PATH_PARMAMETER_ID) int id) {

        logger.log(Level.INFO, "id : {0}", id);

        return ("id : " + id);
    }

    @Path("/longitude/{longitude: [0-9.]+}/latitude/{latitude: [0-9.]+}")
    @GET
    @Produces("application/json")
    public String getJson(@PathParam(PATH_PARMAMETER_LONGITUDE) double longitude,
            @PathParam(PATH_PARMAMETER_LATITUDE) double latitude) {

        logger.log(Level.INFO, "longitude : {0}, latitude : {1}", new Object[]{longitude, latitude});

        return ("longitude : " + longitude + ", latitude : " + latitude);
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
