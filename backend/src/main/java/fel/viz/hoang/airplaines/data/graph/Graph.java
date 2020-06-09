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
    private static double COMPABILITY_THRESHOLD = 0.001;

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

            left.setLeft(null);
            left.setRight(right);
            right.setLeft(left);
            right.setRight(null);
        }

    }

    public void bundleEdges() {
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {

            for (Flight flight : flights) {
                flight.doubleEdges();
            }
            double[][] matrix = getCompabilityMatrix();

            for (int j = 0; j < NUMBER_OF_FLIGHTS; j++) {
                movePointsOfTwoEdges(flights[j], j, matrix);
            }
        }
    }

    private double[][] getCompabilityMatrix() {
        double[][] matrix = new double[NUMBER_OF_FLIGHTS][NUMBER_OF_FLIGHTS];
        double cAngle, cScale, cPosition, cVisibility;
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
                    matrix[i][j] = cAngle * cScale * cPosition * cVisibility;
                } else {
                    matrix[i][j] = 0;
                }
            }
        }
        return matrix;
    }

    private void movePointsOfTwoEdges(Flight p, int index, double[][] matrix) {
        double fP = 0, leftDist = 0, rightDist = 0, sum = 0;
        double[] direction;
        for (int i = 1; i < p.getPoints().size() - 1; i++) {
            sum = 0;
            direction = new double[2];
            leftDist = Math.sqrt(Math.pow(p.getPoints().get(i).getX() - p.getPoints().get(i - 1).getX(), 2) + Math.pow(p.getPoints().get(i).getY() - p.getPoints().get(i - 1).getY(), 2));

            rightDist = Math.sqrt(Math.pow(p.getPoints().get(i + 1).getX() - p.getPoints().get(i).getX(), 2) + Math.pow(p.getPoints().get(i + 1).getY() - p.getPoints().get(i).getY(), 2));
            for (int j = 0; j < NUMBER_OF_FLIGHTS; j++) {
                if (j != index && matrix[index][j] >= COMPABILITY_THRESHOLD) {
                    direction[0] += matrix[index][j] * flights[j].getPoints().get(i).getX() - matrix[index][j] * p.getPoints().get(i).getX();
                    direction[1] += matrix[index][j] * flights[j].getPoints().get(i).getY() - matrix[index][j] * p.getPoints().get(i).getY();
                    sum += 1 / Math.sqrt(Math.pow(flights[j].getPoints().get(i).getX() - p.getPoints().get(i).getX(), 2) + Math.pow(flights[j].getPoints().get(i).getY() - p.getPoints().get(i).getY(), 2));
                }
            }
            fP = (p.getPoints().get(i).getkP() * (leftDist + rightDist)) + sum;
            p.getPoints().get(i).setX(p.getPoints().get(i).getX() + fP * direction[0]);
            p.getPoints().get(i).setY(p.getPoints().get(i).getY() + fP * direction[1]);
        }
    }

    public Airport[] getAirports() {
        return airports;
    }
}