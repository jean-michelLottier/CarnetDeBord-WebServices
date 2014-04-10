/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.ticket;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public class WorkingRadius {

    public static final long DEFAULT_WORKING_RADIUS = 2;

    public enum distanceType {

        KILOMETER, METER, CENTIMETER;
    };

    public enum radiusType {

        RADIUS, DIAMETER;
    };

    private long value;
    private distanceType distType;
    private radiusType radType;

    public WorkingRadius() {
        this.value = DEFAULT_WORKING_RADIUS;
        this.distType = distanceType.KILOMETER;
        this.radType = radiusType.RADIUS;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public distanceType getDistType() {
        return distType;
    }

    public void setDistType(distanceType distType) {
        this.distType = distType;
    }

    public radiusType getRadType() {
        return radType;
    }

    public void setRadType(radiusType radType) {
        this.radType = radType;
    }
}
