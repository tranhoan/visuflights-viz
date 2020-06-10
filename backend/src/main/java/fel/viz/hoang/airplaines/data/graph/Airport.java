/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fel.viz.hoang.airplaines.data.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 *
 * @author Stefan
 */
public class Airport {
    
    static final double X_UPPER_BOUND = -688.16667, X_LOWER_BOUND = -1242.5, Y_UPPER_BOUND = -245.5, Y_LOWER_BOUND = -488.0;
    static final int MIN_AIRPORT_SIZE = 2, MAX_AIRPORT_SIZE = 258;
    
    private double x, y, relativeX, relativeY;
    private String tooltip;
    private String abbreviation;
    private ArrayList<Flight> arrivals, departures;

    public Airport(double x, double y, String tooltip) {
        this.x = x;
        this.y = y;
        this.tooltip = tooltip;
        this.abbreviation = this.tooltip.substring(0, 3);
        this.arrivals = new ArrayList<>();
        this.departures = new ArrayList<>();
        this.relativeX = (this.x - X_LOWER_BOUND)/(X_UPPER_BOUND - X_LOWER_BOUND);
        this.relativeY = (this.y - Y_LOWER_BOUND)/(Y_UPPER_BOUND - Y_LOWER_BOUND);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRelativeX() {
        return relativeX;
    }

    public double getRelativeY() {
        return relativeY;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getAbbreviation() {
        return abbreviation;
    }


    public ArrayList<Flight> getArrivals() {
        return arrivals;
    }

    public ArrayList<Flight> getDepartures() {
        return departures;
    }

    public void addArrival(Flight flight) {
        arrivals.add(flight);
    }

    public void addDeparture(Flight flight) {
        departures.add(flight);
    }

    public int getAirportSize() {
        return arrivals.size() + departures.size();
    }

}
