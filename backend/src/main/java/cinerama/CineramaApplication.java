package cinerama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("cinerama.entity")
@EnableJpaRepositories("cinerama.repository")
public class CineramaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CineramaApplication.class, args);
    }
}