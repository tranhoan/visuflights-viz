package fel.viz.hoang.airplaines;

import fel.viz.hoang.airplaines.data.DataParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AirplainesApplication {

	public static void main(String[] args) {
		System.out.println("Loading Data...");
		DataParser.getData();
		System.out.println("Data Loaded");
		SpringApplication.run(AirplainesApplication.class, args);
	}

}
