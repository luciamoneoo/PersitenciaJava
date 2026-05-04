package pio.daw.ra9.ejemplo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Punto de entrada de la aplicación RA9.
 *
 * @EntityScan y @EnableJpaRepositories limitan el escaneo al paquete pio.daw.ra9
 * para que Spring Boot no intente gestionar las entidades de RA8 (ObjectDB).
 *
 * Arrancar: mvn spring-boot:run  (requiere MySQL en Docker activo)
 *   docker compose up -d
 */
@SpringBootApplication
@EntityScan("pio.daw.ra9.modelo")
@EnableJpaRepositories("pio.daw.ra9.repositorio")
public class Ra9Application {

    public static void main(String[] args) {
        SpringApplication.run(Ra9Application.class, args);
    }
}
