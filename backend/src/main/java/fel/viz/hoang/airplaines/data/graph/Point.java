/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fel.viz.hoang.airplaines.data.graph;

/**
 *
 * @author Stefan
 */
public class Point {

    static final double X_UPPER_BOUND = -688.16667, X_LOWER_BOUND = -1242.5, Y_UPPER_BOUND = -245.5, Y_LOWER_BOUND = -488.0;
    private double x, y, kP, relativeX, relativeY;

    /**
     * Use only for creation of the first two points of any line (the two broder
     * points).
     *
     * @param flight
     * @param number
     */
    public Point(Flight flight, int number) {
        if (number == 0) {
            this.x = flight.getSource().getX();
            this.y = flight.getSource().getY();
        } else {
            this.x = flight.getTarget().getX();
            this.y = flight.getTarget().getY();
        }
        this.relativeX = (this.x - X_LOWER_BOUND)/(X_UPPER_BOUND - X_LOWER_BOUND);
        this.relativeY = (this.y - Y_LOWER_BOUND)/(Y_UPPER_BOUND - Y_LOWER_BOUND);
        this.kP = Graph.K / Math.sqrt(Math.pow(flight.getTarget().getX() - flight.getSource().getX(), 2) + Math.pow(flight.getTarget().getY() - flight.getSource().getY(), 2));
    }

    public Point(Flight fligth, Point left, Point right) {
        double x_div = (right.getX() - left.getX()) / 2;
        double y_div = (right.getY() - left.getY()) / 2;
        this.x = x_div + left.getX();
        this.y = y_div + left.getY();
        this.kP = left.kP;
        this.relativeX = (this.x - X_LOWER_BOUND)/(X_UPPER_BOUND - X_LOWER_BOUND);
        this.relativeY = (this.y - Y_LOWER_BOUND)/(Y_UPPER_BOUND - Y_LOWER_BOUND);
//        left.setRight(this);
//        right.setLeft(this);
//        this.left = left;
//        this.right = right;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getkP() {
        return kP;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRelativeX() {
        return (this.x - X_LOWER_BOUND)/(X_UPPER_BOUND - X_LOWER_BOUND);
    }

    public double getRelativeY() {
        return (this.y - Y_LOWER_BOUND)/(Y_UPPER_BOUND - Y_LOWER_BOUND);
    }

}
