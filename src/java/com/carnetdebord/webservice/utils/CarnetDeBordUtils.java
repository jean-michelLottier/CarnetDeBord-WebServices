/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.utils;

import com.carnetdebord.webservice.entities.Geolocation;
import com.carnetdebord.webservice.entities.Ticket;
import com.carnetdebord.webservice.entities.User;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public class CarnetDeBordUtils {

    public static final Logger logger = Logger.getLogger(CarnetDeBordUtils.class.getName());

    public static final int EARTH_RADIUS = 6371;
    public static final int KILOMETER_IN_METER = 1000;
    public static final int KILOMETER_IN_CENTIMETER = 100000;

    public static final String ADDRESS = "address";
    public static final String ANNEX_INFO = "annexInfo";
    public static final String BIRTH_DATE = "birthDate";
    public static final String CREATION_DATE = "creationDate";
    public static final String FIRST_NAME = "firstName";
    public static final String FIRST_VISITED_DATE = "firstVisitedDate";
    public static final String GEOLOCATION_ID = "geolocationID";
    public static final String LAST_VISITED_DATE = "lastVisitedDate";
    public static final String LATITUDE = "latitude";
    public static final String LOGIN = "login";
    public static final String LONGITUDE = "longitude";
    public static final String MESSAGE = "message";
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String POSTED_DATE = "postedDate";
    public static final String RELEVANCE = "relevance";
    public static final String STATE = "state";
    public static final String TICKET_ID = "ticketID";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String USER_ID = "userID";

    public static enum codeConnection {

        ERROR_LOGIN, ERROR_PASSWORD, ERROR_EMPTY_FIELD, SUCCESS, ERROR_REGISTER, ERROR_ACCOUNT_ALREADY_EXIST, ERROR_ACCOUNT_NOT_ACTIVATED;
    };

    /**
     * <p>
     * This method verify if distance between two geographical points is minor
     * than the radius specify in input parameter.</p>
     *
     * @param long1
     * @param lat1
     * @param long2
     * @param lat2
     * @param r (radius)
     * @return true if the distance calculated is minor than radius, otherwise
     * false.
     */
    public boolean isLocationIntoWorkingRadius(double long1, double lat1, double long2, double lat2, double r) {
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        long1 = Math.toRadians(long1);
        long2 = Math.toRadians(long2);

        double d = EARTH_RADIUS * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(long2 - (long1)));
        return d <= r;
    }

    public class Json<T> {

        private T data;

        public String generateJson() {
            if (data instanceof List) {
                List<Object> temp = (List<Object>) data;
                if (temp != null && !temp.isEmpty() && temp.get(0) instanceof Geolocation) {
                    List<JSONObject> jsonList = new ArrayList<>();
                    for (Object g : temp) {
                        System.out.println(fillJson((Geolocation) g).toJSONString());
                        jsonList.add(fillJson((Geolocation) g));
                    }
                    return JSONValue.toJSONString(jsonList);
                }
                return JSONValue.toJSONString(data);
            } else if (data instanceof Ticket) {
                Ticket t = (Ticket) data;
                JSONObject json = fillJson(t, true);
                return json.toJSONString();
            } else if (data instanceof User) {
                User u = (User) data;
                JSONObject json = fillJson(u);
                return json.toJSONString();
            }

            return null;
        }

        public void set(T t) {
            data = t;
        }

        public T get() {
            return data;
        }

        private JSONObject fillJson(Ticket t, boolean addGeolocInfo) {
            JSONObject json = new JSONObject();
            json.put(USER_ID, t.getUserFK().getId());
            json.put(TICKET_ID, t.getId());
            json.put(ANNEX_INFO, t.getAnnexInfo());
            json.put(MESSAGE, t.getMessage());
            json.put(POSTED_DATE, t.getPostedDate().toString());
            json.put(RELEVANCE, t.getRelevance());
            json.put(STATE, t.getState());
            json.put(TITLE, t.getTitle());
            json.put(TYPE, t.getType());
            if (addGeolocInfo) {
                for (Geolocation g : t.getGeolocationCollection()) {
                    json.put(GEOLOCATION_ID, g.getId());
                    json.put(LATITUDE, g.getLatitude());
                    json.put(LONGITUDE, g.getLongitude());
                    json.put(ADDRESS, g.getAddress());
                    break;
                }
            }

            return json;
        }

        private JSONObject fillJson(Geolocation geolocation) {
            JSONObject json = new JSONObject();
            json.put(GEOLOCATION_ID, geolocation.getId());
            json.put(LATITUDE, geolocation.getLatitude());
            json.put(LONGITUDE, geolocation.getLongitude());
            json.put(ADDRESS, geolocation.getAddress());
            json.putAll(fillJson(geolocation.getTicketFK(), false));
            json.put(NAME, geolocation.getTicketFK().getUserFK().getName());
            json.put(FIRST_NAME, geolocation.getTicketFK().getUserFK().getFirstname());

            return json;
        }

        private JSONObject fillJson(User user) {
            JSONObject json = new JSONObject();
            json.put(USER_ID, user.getId());
            json.put(BIRTH_DATE, user.getBirthDate().toString());
            json.put(CREATION_DATE, user.getCreationDate().toString());
            json.put(FIRST_NAME, user.getFirstname());
            json.put(LOGIN, user.getLogin());
            json.put(NAME, user.getName());
            json.put(PASSWORD, user.getPassword());

            return json;
        }
    }

}
