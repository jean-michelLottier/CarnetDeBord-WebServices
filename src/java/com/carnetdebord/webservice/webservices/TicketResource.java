/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.webservices;

import com.carnetdebord.webservice.entities.Geolocation;
import com.carnetdebord.webservice.entities.Historical;
import com.carnetdebord.webservice.entities.Ticket;
import com.carnetdebord.webservice.entities.User;
import com.carnetdebord.webservice.login.ILoginService;
import com.carnetdebord.webservice.login.LoginService;
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
import static com.carnetdebord.webservice.webservices.LoginResource.logger;
import java.util.Date;
import java.util.LinkedHashMap;
import javax.ws.rs.POST;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    private static final String PARAMETER_TICKET_ID = "ticketID";
    private static final String PARAMETER_USER_ID = "userID";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_TYPE = "type";
    private static final String PARAMETER_ANNEX_INFO = "annexInfo";
    private static final String PARAMETER_STATE = "state";
    private static final String PARAMETER_GEOLOCATION_ID = "geolocationID";
    private static final String PARAMETER_ADDRESS = "address";

    @Context
    private UriInfo context;

    private ITicketService ticketService;
    private ILoginService loginService;

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

        List<Historical> historicals = ticketService.whoViewedTicket(ticketID);

        LinkedHashMap jsonMap = new LinkedHashMap();
        jsonMap.put("ticket", json.generateJson());
        JSONArray jsona = new JSONArray();
        if (historicals != null) {
            for (Historical h : historicals) {
                JSONObject jsono = new JSONObject();
                jsono.put("userID", h.getUserFK().getId());
                jsono.put("ticketID", h.getTicketFK().getId());
                jsono.put("firstVisitedDate", h.getFirstVisitedDate().toString());
                jsono.put("lastVisitedDate", h.getLastVisitedDate().toString());
                jsona.add(jsono.toJSONString());
            }
        }
        jsonMap.put("monitoring", jsona.toJSONString());

        JSONObject jsono = new JSONObject();
        jsono.putAll(jsonMap);

        return Response.ok(jsono.toJSONString()).build();
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

        return Response.ok(json.generateJson()).build();
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

    /**
     * <p>
     * PUT request to save a ticket.</p>
     *
     * @param content
     * @return
     */
    @POST
    @Consumes("application/json")
    public Response postJson(String content) {
        Response.ResponseBuilder response = Response.ok();
        if (content == null) {
            response.status(Response.Status.BAD_REQUEST);
            return response.build();
        }

        Geolocation geolocation = new Geolocation();
        Ticket ticket = new Ticket();
        long userID;
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(content);
            ticket.setTitle(StringEscapeUtils.escapeXml(json.get(PARAMETER_TITLE).toString().trim()));
            ticket.setMessage(StringEscapeUtils.escapeXml(json.get(PARAMETER_MESSAGE).toString().trim()));
            ticket.setType(StringEscapeUtils.escapeXml(json.get(PARAMETER_TYPE).toString()).trim());
            ticket.setState(Boolean.valueOf(json.get(PARAMETER_STATE).toString()));
            ticket.setAnnexInfo(StringEscapeUtils.escapeXml(json.get(PARAMETER_ANNEX_INFO).toString().trim()));
            ticket.setRelevance(0);
            ticket.setPostedDate(new Date());
            geolocation.setTicketFK(ticket);
            geolocation.setLatitude(Float.valueOf(json.get(PATH_PARMAMETER_LATITUDE).toString()));
            geolocation.setLongitude(Float.valueOf(json.get(PATH_PARMAMETER_LONGITUDE).toString()));
            geolocation.setAddress(StringEscapeUtils.escapeXml(json.get(PARAMETER_ADDRESS).toString()));
            userID = Long.valueOf(json.get(PARAMETER_USER_ID).toString());
        } catch (ParseException e) {
            logger.log(Level.WARNING, "Impossible to parse content in json object.", e);
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return response.build();
        }

        loginService = new LoginService();
        User user = loginService.findUserById(userID);
        if (user == null) {
            logger.log(Level.WARNING, "Impossible to find user information with id = {0}", userID);
            response.status(Response.Status.BAD_REQUEST);
            return response.build();
        }
        ticket.setUserFK(user);

        ticketService = new TicketService();
        ticketService.saveTicket(ticket);
        ticketService.saveTicketWithGeolocation(geolocation);

        return response.build();
    }
}
