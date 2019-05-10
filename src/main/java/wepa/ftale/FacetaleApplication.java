package wepa.ftale;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Matias
 */
@SpringBootApplication
public class FacetaleApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+2"));
    }

    public static void main(String[] args) {
        SpringApplication.run(FacetaleApplication.class);
    }
}