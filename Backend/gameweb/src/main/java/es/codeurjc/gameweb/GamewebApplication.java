package es.codeurjc.gameweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class GamewebApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(GamewebApplication.class, args);
    }
}