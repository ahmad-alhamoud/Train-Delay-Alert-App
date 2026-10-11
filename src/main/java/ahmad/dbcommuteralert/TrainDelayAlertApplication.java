package ahmad.dbcommuteralert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrainDelayAlertApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainDelayAlertApplication.class, args);
    }

}
