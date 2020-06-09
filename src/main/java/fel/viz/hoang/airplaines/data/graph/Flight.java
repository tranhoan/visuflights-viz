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
public class Flight {

    private Airport source, target;
    private ArrayList<Point> points;

    public Flight(Airport source, Airport target) {
        this.source = source;
        this.target = target;
        this.points = new ArrayList<>();
    }

    @JsonIgnore
    public Airport getSource() {
        return source;
    }
    @JsonIgnore
    public Airport getTarget() {
        return target;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public void doubleEdges() {
        Point[] newPoints = new Point[points.size() - 1];
        for (int i = 0; i < newPoints.length; i++) {
            newPoints[i] = new Point(this, points.get(i), points.get(i + 1));
        }
        for (int i = newPoints.length - 1; i >= 0; i--) {
            points.add(i+1, newPoints[i]);
        }
    }

}
