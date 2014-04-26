/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.webservices;

import com.carnetdebord.webservice.email.EmailService;
import com.carnetdebord.webservice.email.IEmailService;
import com.carnetdebord.webservice.entities.User;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import com.carnetdebord.webservice.login.ILoginService;
import com.carnetdebord.webservice.login.LoginService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.carnetdebord.webservice.utils.CarnetDeBordUtils;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import javax.ws.rs.PathParam;

/**
 * REST Web Service
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Path("login")
public class LoginResource extends CarnetDeBordUtils {

    public static final Logger logger = Logger.getLogger(LoginResource.class.getName());
    private static final String PARAMETER_LOGIN = "login";
    private static final String PARAMETER_PASSWORD = "password";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_FIRSTNAME = "firstname";
    private static final String PARAMETER_BIRTHDATE = "birthdate";
    private static final String PATH_PARAMETER_TOKEN_ID = "tokenid";

    @Context
    private UriInfo context;

    private ILoginService loginService;
    private IEmailService emailService;

    public ILoginService getLoginService() {
        return loginService;
    }

    public void setLoginService(ILoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Creates a new instance of LoginResource
     */
    public LoginResource() {
    }

    /**
     * <p>
     * <strong>GET</strong> request to activate account.</p>
     *
     * @param tokenID
     * @return an instance of java.lang.String
     */
    @Path("/token/{tokenid: ([a-zA-Z0-9=]+)}")
    @GET
    @Produces("application/json")
    public Response getJson(@PathParam(PATH_PARAMETER_TOKEN_ID) String tokenID) {
        logger.info("request GET");
        Response.ResponseBuilder response = Response.ok();

        if (tokenID == null || tokenID.isEmpty()) {
            response.status(Response.Status.BAD_REQUEST);
            return response.build();
        }

        tokenID = new String(Base64.decode(tokenID), Charset.defaultCharset());
        loginService = new LoginService();
        codeConnection connection = loginService.activateAccount(tokenID);

        if (connection.equals(codeConnection.ERROR_LOGIN)) {
            response.status(Response.Status.BAD_REQUEST);
            return response.build();
        }

        try {
            return Response.temporaryRedirect(new URI("http://serveur10.lerb.polymtl.ca:8080/CarnetDeBord/confirmation-inscription.html")).build();
        } catch (URISyntaxException e) {
            logger.log(Level.WARNING, "uri no available", e);
        }
        return response.build();
    }

    /**
     * <p>
     * <strong>GET</strong> request to generate new password when you forgot
     * this one.</p>
     *
     * @param login
     * @return
     */
    @Path("/{login: ([a-zA-Z0-9.-]+)(@)([a-z]+)(\\.)([a-z]{2,3})}")
    @GET
    @Produces("application/json")
    public Response getJson2(@PathParam(PARAMETER_LOGIN) String login) {
        Response.ResponseBuilder response = Response.ok();

        logger.log(Level.INFO, "login : {0}", login);

        loginService = new LoginService();
        codeConnection connection = loginService.generatePassword(login);
        if (connection.equals(codeConnection.ERROR_LOGIN)) {
            response.status(Response.Status.BAD_REQUEST);
        }

        return response.build();
    }

    /**
     * PUT method for updating or creating an instance of LoginResource
     *
     * @param content representation for the resource
     * @return Response with adapted statu according to result.
     */
    @PUT
    @Consumes("application/json")
    public Response putJson(String content) {
        logger.info("request PUT");
        Response.ResponseBuilder response = Response.noContent();
        if (content == null) {
            response.status(Response.Status.BAD_REQUEST);
            return response.build();
        }

        String login, password;
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(content);
            login = StringEscapeUtils.escapeXml(json.get(PARAMETER_LOGIN).toString());
            password = StringEscapeUtils.escapeXml(json.get(PARAMETER_PASSWORD).toString());
        } catch (ParseException e) {
            logger.log(Level.WARNING, "Impossible to parse content in json object.", e);
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return response.build();
        }

        loginService = new LoginService();
        codeConnection connection = loginService.isLoginPasswordCorrect(login, password);

        if (connection.equals(codeConnection.ERROR_LOGIN)
                || connection.equals(codeConnection.ERROR_PASSWORD)
                || connection.equals(codeConnection.ERROR_ACCOUNT_NOT_ACTIVATED)) {
            response.status(Response.Status.UNAUTHORIZED);
            return response.build();
        } else if (connection.equals(codeConnection.ERROR_EMPTY_FIELD)) {
            response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE);
            return response.build();
        }

        User user = loginService.getUserInformation(login);
        Json<User> json = new Json<>();
        json.set(user);

        return Response.ok(json.generateJson()).build();
    }

    /**
     * <p>
     * POST method to create new user into database.</p>
     *
     * @param content
     * @return Response with adapted statu according to result.
     */
    @POST
    @Consumes("application/json")
    public Response postJson(String content) {
        logger.info("request POST");
        Response.ResponseBuilder response = Response.noContent();
        if (content == null) {
            response.status(Response.Status.BAD_REQUEST);
            return response.build();
        }

        User user = new User();
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(content);
            user.setLogin(StringEscapeUtils.escapeXml(json.get(PARAMETER_LOGIN).toString()));
            user.setPassword(StringEscapeUtils.escapeXml(json.get(PARAMETER_PASSWORD).toString()));
            user.setName(StringEscapeUtils.escapeXml(json.get(PARAMETER_NAME).toString()));
            user.setFirstname(StringEscapeUtils.escapeXml(json.get(PARAMETER_FIRSTNAME).toString()));
            String birthDateStr = json.get(PARAMETER_BIRTHDATE).toString();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Canada/Atlantic"));
            user.setCreationDate(calendar.getTime());
            calendar.set(Integer.valueOf(birthDateStr.split("/")[0]), Integer.valueOf(birthDateStr.split("/")[1]) - 1, Integer.valueOf(birthDateStr.split("/")[2]) + 1, 0, 0, 0);
            user.setBirthDate(calendar.getTime());
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "A field has not been correctly filled", e);
            response.status(Response.Status.BAD_REQUEST);
            return response.build();
        } catch (ParseException e) {
            logger.log(Level.SEVERE, "Impossible to parse content in json object.", e);
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return response.build();
        }

        loginService = new LoginService();
        codeConnection connection = loginService.createNewUser(user);

        if (connection.equals(codeConnection.ERROR_REGISTER)) {
            logger.log(Level.SEVERE, "Impossible to register user's data into database");
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return response.build();
        } else if (connection.equals(codeConnection.ERROR_ACCOUNT_ALREADY_EXIST)) {
            logger.log(Level.WARNING, "Account ''{0}'' already exist.", user.getLogin());
            response.status(Response.Status.CONFLICT);
            return response.build();
        }

        user = loginService.getUserInformation(user.getLogin());
        Json<User> j = new Json<>();
        j.set(user);

        String token = Base64.encode(user.getLogin().getBytes(Charset.defaultCharset()));
        emailService = new EmailService();
        emailService.sendConfirmationEmailWithGmail(token, user);

        return Response.ok(j.generateJson()).build();
    }
}
