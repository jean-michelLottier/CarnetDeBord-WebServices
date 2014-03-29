/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public class CarnetDeBordUtils {

    public boolean isLocationIntoWorkingRadius(double x, double y, double a, double b, double r) {
        return Math.pow(x - a, 2) + Math.pow(y - b, 2) <= r;
    }
}
