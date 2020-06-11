package fel.viz.hoang.airplaines.data;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.*;

import fel.viz.hoang.airplaines.data.graph.Graph;
import fel.viz.hoang.airplaines.data.graph.Point;
import java.io.FileWriter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DataParser {

    private static Graph graph;

    public static Graph getData(){
        if(Objects.isNull(graph)){
            System.out.println("First retrieval");
            parseData();
            return graph;
        }else{
            System.out.println("Repeated retrieval");
            return graph;
        }
    }

    public static Graph parseData() {
        try {
            File inputFile = new File("airlines.graphml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;

            dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            Graph gr = new Graph(doc);
            
            
            gr.bundleEdges();
            
            /*
            FileWriter writer = new FileWriter("lines.csv");
            double x, y;
            int scale = 2;
            for (int i = 0; i < gr.getFlights()[0].getPoints().size(); i++) {
                for (int j = 0; j < 20 ; j++) {
                    x = Math.round(gr.getFlights()[j].getPoints().get(i).getX() * Math.pow(10, scale)) / Math.pow(10, scale);
                    y = Math.round(gr.getFlights()[j].getPoints().get(i).getY() * Math.pow(10, scale)) / Math.pow(10, scale);
                    writer.append(x + ";" + y + ";");
                }
                writer.append("\n");
                writer.flush();
            }
            writer.close();
            */
            
            graph = gr;
            
            

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DataParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(DataParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }
}
