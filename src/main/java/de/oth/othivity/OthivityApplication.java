package de.oth.othivity;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OthivityApplication {

	public static void main(String[] args) {
        try { Dotenv dotenv = Dotenv.load();
            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );
        } catch (Exception e) {
           System.err.println("Could not load .env file, make sure environment variables are set properly.");
        }

		SpringApplication.run(OthivityApplication.class, args);
	}

}
