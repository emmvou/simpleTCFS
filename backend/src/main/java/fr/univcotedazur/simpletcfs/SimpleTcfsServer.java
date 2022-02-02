package fr.univcotedazur.simpletcfs;

import fr.univcotedazur.simpletcfs.components.BankProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class SimpleTcfsServer {

    public static void main(String[] args) {
        SpringApplication.run(SimpleTcfsServer.class, args);
    }

}
