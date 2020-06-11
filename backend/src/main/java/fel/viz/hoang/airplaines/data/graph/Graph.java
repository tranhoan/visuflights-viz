/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fel.viz.hoang.airplaines.data.graph;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Stefan
 */
public class Graph {

    private static final int NUMBER_OF_AIRPORTS = 235, NUMBER_OF_FLIGHTS = 2101, NUMBER_OF_ITERATIONS = 5;
    public static final double K = 0.1;
    private static final double COMPABILITY_THRESHOLD = 0.00002, COMPABILITY_MULTIPLIER = 500, MAXIMUM_CHANGE = 0.95;

    public static double maxX, maxY, sumX, sumY, counter;

    private Airport[] airports;
    private Flight[] flights;

    public Graph(Document doc) {
        int minSize = Integer.MAX_VALUE, maxSize = 0;
        this.airports = new Airport[NUMBER_OF_AIRPORTS];
        NodeList nodes = doc.getElementsByTagName("node");
        Node item;
        for (int i = 0; i < NUMBER_OF_AIRPORTS; i++) {
            item = nodes.item(i);
            NodeList children = item.getChildNodes();
            airports[i] = new Airport(Double.parseDouble(children.item(1).getTextContent()), Double.parseDouble(children.item(5).getTextContent()), children.item(3).getTextContent());

        }
        this.flights = new Flight[NUMBER_OF_FLIGHTS];
        nodes = doc.getElementsByTagName("edge");
        NamedNodeMap attridubtes;
        int source, target;
        for (int i = 0; i < NUMBER_OF_FLIGHTS; i++) {
            item = nodes.item(i);
            attridubtes = item.getAttributes();
            source = Integer.parseInt(attridubtes.getNamedItem("source").getNodeValue());
            target = Integer.parseInt(attridubtes.getNamedItem("target").getNodeValue());
            flights[i] = new Flight(airports[source], airports[target]);
            airports[source].addDeparture(flights[i]);
            airports[target].addArrival(flights[i]);
        }
        for (int i = 0; i < airports.length; i++) {
            if (minSize > airports[i].getAirportSize()) {
                minSize = airports[i].getAirportSize();
            }
            if (maxSize < airports[i].getAirportSize()) {
                maxSize = airports[i].getAirportSize();
            }
        }
        Point left, right;
        for (int i = 0; i < NUMBER_OF_FLIGHTS; i++) {
            left = new Point(flights[i], 0);
            flights[i].addPoint(left);
            right = new Point(flights[i], 1);
            flights[i].addPoint(right);

        }

    }

    public void bundleEdges() {
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            for (Flight flight : flights) {
                flight.doubleEdges(i + 1);
            }
            double[][] compabilityMatrix = getCompabilityMatrix();
            for (int j = 0; j < NUMBER_OF_FLIGHTS; j++) {
                movePointsOfTwoEdges(flights[j], j, compabilityMatrix, i + 1);
            }
            System.out.println("Iteration " + (i + 1) + " max divergence: " + Math.round(maxX) + " " + Math.round(maxY) + " average divergence: " + Math.round(sumX / counter) + " " + Math.round(sumY / counter) + " of " + counter);
            maxX = 0;
            maxY = 0;
            sumX = 0;
            sumY = 0;
            counter = 0;
        }
    }

    private double[][] getCompabilityMatrix() {
        double[][] matrix = new double[NUMBER_OF_FLIGHTS][2];
        double cAngle, cScale, cPosition/*, cVisibility*/;
        double[] p = new double[2], q = new double[2], v = new double[2], i0 = new double[2], i1 = new double[2], iM = new double[2];
        double pSize, qSize, lAvg, distance, a, vPQ, vQP;
        int index;
        for (int i = 0; i < NUMBER_OF_FLIGHTS; i++) {
            for (int j = 0; j < NUMBER_OF_FLIGHTS; j++) {
                if (i != j) {
                    p[0] = flights[i].getTarget().getX() - flights[i].getSource().getX();
                    p[1] = flights[i].getTarget().getY() - flights[i].getSource().getY();
                    q[0] = flights[j].getTarget().getX() - flights[j].getSource().getX();
                    q[1] = flights[j].getTarget().getY() - flights[j].getSource().getY();
                    pSize = Math.sqrt(Math.pow(p[0], 2) + Math.pow(p[1], 2));
                    qSize = Math.sqrt(Math.pow(q[0], 2) + Math.pow(q[1], 2));
                    //cAngle
                    cAngle = Math.abs(Math.cos(Math.acos((p[0] * q[0] + p[1] * q[1]) / (pSize * qSize))));
                    //cScale
                    lAvg = (pSize + qSize) / 2;
                    cScale = 2 / ((lAvg * Math.min(pSize, qSize)) + (Math.max(pSize, qSize) / lAvg));
                    //cPosition
                    index = flights[i].getPoints().size() / 2;
                    Point pPoint = flights[i].getPoints().get(index), qPoint = flights[j].getPoints().get(index);
                    distance = Math.sqrt(Math.pow(qPoint.getX() - pPoint.getX(), 2) + Math.pow(qPoint.getY() - pPoint.getY(), 2));
                    cPosition = lAvg / (lAvg + distance);
                    /*
                    //cVisibility
                    v[0] = flights[j].getSource().getX() - flights[i].getSource().getX();
                    v[1] = flights[j].getSource().getY() - flights[i].getSource().getY();
                    a = (p[0] * v[0] + p[1] * v[1]) / pSize;
                    i0[0] = flights[i].getTarget().getX() * (a / pSize);
                    i0[1] = flights[i].getTarget().getY() * (a / pSize);
                    v[0] = flights[i].getTarget().getX() - flights[j].getTarget().getX();
                    v[1] = flights[i].getTarget().getY() - flights[j].getTarget().getY();
                    a = (p[0] * v[0] + p[1] * v[1]) / pSize;
                    i1[0] = flights[i].getTarget().getX() * (pSize / a);
                    i1[1] = flights[i].getTarget().getY() * (pSize / a);
                    if (i0[0] < i1[0]) {
                        iM[0] = i1[0] / 2;
                    } else {
                        iM[0] = i0[0] / 2;
                    }
                    if (i0[1] < i1[1]) {
                        iM[1] = i1[1] / 2;
                    } else {
                        iM[1] = i0[1] / 2;
                    }
                    vPQ = Math.max((1 - (2 * (Math.sqrt(Math.pow(iM[0] - pPoint.getX(), 2) + Math.pow(iM[1] - pPoint.getY(), 2)))) / Math.sqrt(Math.pow(i1[0] - i0[0], 2) + Math.pow(i1[1] - i0[1], 2))), 0);
                    vQP = Math.max((1 - (2 * (Math.sqrt(Math.pow(iM[0] - qPoint.getX(), 2) + Math.pow(iM[1] - qPoint.getY(), 2)))) / Math.sqrt(Math.pow(i1[0] - i0[0], 2) + Math.pow(i1[1] - i0[1], 2))), 0);
                    cVisibility = Math.min(vPQ, vQP);
                     */
                    if (matrix[i][1] <= cAngle * cScale * cPosition) {
                        matrix[i][0] = j;
                        matrix[i][1] = cAngle * cScale * cPosition /* cVisibility*/;
                    }
                }
            }
        }
        return matrix;
    }

    private void movePointsOfTwoEdges(Flight flight, int index, double[][] compabilityMatrix, int iteration) {
        double[] direction = new double[2];
        double distanceToA = 0, distanceToB = 0;
        Point p = null, q = null, left = null, right = null;
        for (int i = 1; i < flight.getPoints().size() - 1; i++) {
            final int flightToCompareIndex = (int) compabilityMatrix[index][0];
            if (compabilityMatrix[index][1] >= COMPABILITY_THRESHOLD * iteration) {

                p = flight.getPoints().get(i);
                left = flight.getPoints().get(i - 1);
                right = flight.getPoints().get(i + 1);
                distanceToA = vectorSize(p, flights[flightToCompareIndex].getPoints().get(i));
                distanceToB = vectorSize(p, flights[flightToCompareIndex].getPoints().get(flights[flightToCompareIndex].getPoints().size() - 1 - i));
                if (distanceToA < distanceToB) {
                    q = flights[flightToCompareIndex].getPoints().get(i);
                } else {
                    q = flights[flightToCompareIndex].getPoints().get(flights[flightToCompareIndex].getPoints().size() - 1 - i);
                }
                direction[0] = (p.getkP() * (p.getX() - left.getX())
                        + p.getkP() * (right.getX() - p.getX())
                        + (q.getX() - p.getX()) * compabilityMatrix[index][1] * COMPABILITY_MULTIPLIER);
                direction[1] = (p.getkP() * (p.getY() - left.getY())
                        + p.getkP() * (right.getY() - p.getY())
                        + (q.getY() - p.getY()) * compabilityMatrix[index][1] * COMPABILITY_MULTIPLIER);

                double distanceToQ = vectorSize(p, q);
                double distaceFromPtoNewP = vectorSize(p, p.getX() + direction[0], p.getY() + direction[1]);
                if (distaceFromPtoNewP > distanceToQ * MAXIMUM_CHANGE) {
                    direction[0] = direction[0] * (distanceToQ * MAXIMUM_CHANGE / distaceFromPtoNewP);
                    direction[1] = direction[1] * (distanceToQ * MAXIMUM_CHANGE / distaceFromPtoNewP);
                }

                if (Math.abs(maxX) < Math.abs(direction[0])) {
                    maxX = direction[0];
                }
                if (Math.abs(maxY) < Math.abs(direction[1])) {
                    maxY = direction[1];
                }
                sumX += Math.abs(direction[0]);
                sumY += Math.abs(direction[1]);
                counter++;
                p.fixIt();
                p.setX(p.getX() + direction[0]);
                p.setY(p.getY() + direction[1]);

            }
        }
    }

    public Airport[] getAirports() {
        return airports;
    }

    public Flight[] getFlights() {
        return flights;
    }

    private double vectorSize(Point a, Point b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    private double vectorSize(Point a, double xB, double yB) {
        return Math.sqrt(Math.pow(a.getX() - xB, 2) + Math.pow(a.getY() - yB, 2));
    }
}
