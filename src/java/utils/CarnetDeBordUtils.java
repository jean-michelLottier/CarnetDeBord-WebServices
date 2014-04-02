/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entities.Geolocation;
import entities.Ticket;
import java.util.ArrayList;
import java.util.Iterator;
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
                return JSONValue.toJSONString(data);
            } else if (data instanceof Ticket) {
                Ticket t = (Ticket) data;
                JSONObject json = fillJson(t);

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

        private JSONObject fillJson(Ticket t) {
            JSONObject json = new JSONObject();
            json.put("ticketID", t.getId());
            json.put("annexInfo", t.getAnnexInfo());
            json.put("message", t.getMessage());
            json.put("postedDate", t.getPostedDate());
            json.put("relevance", t.getRelevance());
            json.put("state", t.getState());
            json.put("title", t.getTitle());
            json.put("type", t.getType());
            for (Geolocation g : t.getGeolocationCollection()) {
                json.put("geolocationID", g.getId());
                json.put("latitude", g.getLatitude());
                json.put("longitude", g.getLongitude());
                json.put("address", g.getAddress());
                break;
            }

            return json;
        }
    }

}
