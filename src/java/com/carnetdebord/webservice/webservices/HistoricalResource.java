/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.webservices;

import com.carnetdebord.webservice.entities.Historical;
import com.carnetdebord.webservice.entities.Ticket;
import com.carnetdebord.webservice.entities.User;
import com.carnetdebord.webservice.ticket.ITicketService;
import com.carnetdebord.webservice.ticket.TicketService;
import com.carnetdebord.webservice.utils.CarnetDeBordUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * REST Web Service
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Path("historical")
public class HistoricalResource extends CarnetDeBordUtils {

    public static final Logger logger = Logger.getLogger(HistoricalResource.class.getName());

    @Context
    private UriInfo context;

    private ITicketService ticketService;

    private static final String PATH_PARAMETER_USER_ID = "userid";
    private static final String PATH_PARAMETER_TICKET_ID = "ticketid";

    /**
     * Creates a new instance of HistoricalResource
     */
    public HistoricalResource() {
    }

    /**
     * Retrieves representation of an instance of
     * com.carnetdebord.webservice.webservices.HistoricalResource
     *
     * @param userID
     * @return an instance of java.lang.String
     */
    @Path("/{userid:(\\d+)}")
    @GET
    @Produces("application/json")
    public Response getJson(@PathParam(PATH_PARAMETER_USER_ID) long userID) {
        Response.ResponseBuilder response = Response.ok();
        if (userID < 0) {
            response.status(Response.Status.UNAUTHORIZED);
            return response.build();
        }

        ticketService = new TicketService();
        List<Ticket> tickets = ticketService.getUserHistorical(userID, 10);
        JSONArray jsona = new JSONArray();
        if (tickets != null) {
            for (Ticket ticket : tickets) {
                Json<Ticket> json = new Json<>();
                json.set(ticket);
                jsona.add(json.generateJson());
            }
        }

        return Response.ok(jsona.toJSONString()).build();
    }

    /**
     * <p>
     * GET request to monitored tickets.</p>
     *
     * @param userID
     * @param ticketID
     * @return
     */
    @Path("/monitoring/{userid:(\\d+)}/ticketid/{ticketid:(\\d+)}")
    @GET
    @Consumes("application/json")
    public Response getJson(@PathParam(PATH_PARAMETER_USER_ID) long userID, @PathParam(PATH_PARAMETER_TICKET_ID) long ticketID) {
        logger.log(Level.INFO, "userid : {0}, ticketid : {1}", new Object[]{userID, ticketID});
        ticketService = new TicketService();
        Ticket ticket = ticketService.findUserTicket(userID, ticketID);
        if (ticket == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<Historical> historicals = ticketService.whoViewedTicket(ticketID);
        JSONArray jsona = new JSONArray();
        if (historicals != null) {
            for (Historical h : historicals) {
                JSONObject json = new JSONObject();
                json.put(USER_ID, h.getUserFK().getId());
                json.put(TICKET_ID, h.getTicketFK().getId());
                json.put(FIRST_VISITED_DATE, h.getFirstVisitedDate().toString());
                json.put(LAST_VISITED_DATE, h.getLastVisitedDate().toString());
                jsona.add(json.toJSONString());
            }
        }

        return Response.ok(jsona.toJSONString()).build();
    }

    /**
     * PUT method for updating or creating an instance of HistoricalResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @POST
    @Consumes("application/json")
    public Response postJson(String content) {
        Response.ResponseBuilder response = Response.ok();
        if (content == null || content.isEmpty()) {
            return response.build();
        }

        Historical historical = new Historical();
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(content);
            historical.setUserFK(new User(Integer.valueOf(json.get(USER_ID).toString())));
            historical.setTicketFK(new Ticket(Integer.valueOf(json.get(TICKET_ID).toString())));
            DateFormat format = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            String date = json.get(FIRST_VISITED_DATE).toString();
            historical.setFirstVisitedDate(format.parse(date));
            historical.setLastVisitedDate(format.parse(date));
        } catch (ParseException | java.text.ParseException ex) {
            Logger.getLogger(HistoricalResource.class.getName()).log(Level.SEVERE, null, ex);
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return response.build();
        }

        ticketService = new TicketService();
        ticketService.addTicketConsultedIntoHistorical(historical);

        return response.build();
    }
}
