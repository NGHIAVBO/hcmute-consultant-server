package HcmuteConsultantServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HcmuteConsultantServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HcmuteConsultantServerApplication.class, args);
    }
}
