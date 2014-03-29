/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices;

import entities.User;
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
import login.ILoginService;
import login.LoginService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * REST Web Service
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Path("login")
public class LoginResource {

    public static final Logger logger = Logger.getLogger(LoginResource.class.getName());
    private static final String PARAMETER_LOGIN = "login";
    private static final String PARAMETER_PASSWORD = "password";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_FIRSTNAME = "firstname";
    private static final String PARAMETER_BIRTHDATE = "birthdate";

    @Context
    private UriInfo context;

    private ILoginService loginService;

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
     * Retrieves representation of an instance of webservices.LoginResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        logger.info("request GET");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", "tata");
        jsonObject.put("password", "09876");

        return jsonObject.toJSONString();
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
        ILoginService.codeConnection connection = loginService.isLoginPasswordCorrect(login, password);

        if (connection.equals(ILoginService.codeConnection.ERROR_LOGIN)
                || connection.equals(ILoginService.codeConnection.ERROR_PASSWORD)) {
            response.status(Response.Status.UNAUTHORIZED);
        } else if (connection.equals(ILoginService.codeConnection.ERROR_EMPTY_FIELD)) {
            response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE);
        }

        return response.build();
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
        ILoginService.codeConnection connection = loginService.createNewUser(user);

        if (connection.equals(ILoginService.codeConnection.ERROR_REGISTER)) {
            logger.log(Level.SEVERE, "Impossible to register user's data into database");
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return response.build();
        } else if (connection.equals(ILoginService.codeConnection.ERROR_ACCOUNT_ALREADY_EXIST)) {
            logger.log(Level.WARNING, "Account ''{0}'' already exist.", user.getLogin());
            response.status(Response.Status.CONFLICT);
            return response.build();
        }

        return response.build();
    }
}
