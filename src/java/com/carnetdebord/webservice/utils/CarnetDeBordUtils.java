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

    public static enum codeConnection {

        ERROR_LOGIN, ERROR_PASSWORD, ERROR_EMPTY_FIELD, SUCCESS, ERROR_REGISTER, ERROR_ACCOUNT_ALREADY_EXIST;
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
            if (data instanceof ArrayList) {
                ArrayList<Object> temp = (ArrayList<Object>) data;
                if (temp != null && !temp.isEmpty() && temp.get(0) instanceof Geolocation) {
                    List<JSONObject> jsonList = new ArrayList<>();
                    for (Object g : temp) {
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
            json.put("ticketID", t.getId());
            json.put("annexInfo", t.getAnnexInfo());
            json.put("message", t.getMessage());
            json.put("postedDate", t.getPostedDate());
            json.put("relevance", t.getRelevance());
            json.put("state", t.getState());
            json.put("title", t.getTitle());
            json.put("type", t.getType());
            if (addGeolocInfo) {
                for (Geolocation g : t.getGeolocationCollection()) {
                    json.put("geolocationID", g.getId());
                    json.put("latitude", g.getLatitude());
                    json.put("longitude", g.getLongitude());
                    json.put("address", g.getAddress());
                    break;
                }
            }

            return json;
        }

        private JSONObject fillJson(Geolocation geolocation) {
            JSONObject json = new JSONObject();
            json.put("geolocationID", geolocation.getId());
            json.put("latitude", geolocation.getLatitude());
            json.put("longitude", geolocation.getLongitude());
            json.put("address", geolocation.getAddress());
            json.putAll(fillJson(geolocation.getTicketFK(), false));
            json.put("name", geolocation.getTicketFK().getUserFK().getName());
            json.put("firstname", geolocation.getTicketFK().getUserFK().getFirstname());

            return json;
        }

        private JSONObject fillJson(User user) {
            JSONObject json = new JSONObject();
            json.put("userID", user.getId());
            json.put("birthDate", user.getBirthDate());
            json.put("creationDate", user.getCreationDate());
            json.put("firstName", user.getFirstname());
            json.put("login", user.getLogin());
            json.put("name", user.getName());
            json.put("password", user.getPassword());

            return json;
        }
    }

}