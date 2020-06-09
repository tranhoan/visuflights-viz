package fel.viz.hoang.airplaines.rest;
import fel.viz.hoang.airplaines.data.DataParser;
import fel.viz.hoang.airplaines.data.graph.Graph;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {

    @GetMapping("/data")
    public Graph getData(){
        return DataParser.getData();
    }
}
