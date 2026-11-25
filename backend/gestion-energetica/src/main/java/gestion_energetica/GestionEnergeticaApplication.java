
package gestion_energetica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestionEnergeticaApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            GestionEnergeticaApplication.class, args
        );
    }
}
