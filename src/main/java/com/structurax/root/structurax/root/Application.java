package com.structurax.root.structurax.root;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import io.github.cdimascio.dotenv.Dotenv;

@EnableAsync // Enables @Async functionality
//@EnableCaching
@SpringBootApplication
@ComponentScan(basePackages = "com.structurax.root.structurax.root")
public class Application {

	public static void main(String[] args) {
		// Load .env variables
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMalformed()
				.ignoreIfMissing()
				.load();

		// Set system properties so Spring can read them using ${...}
		System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
		System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));

		SpringApplication.run(Application.class, args);
	}
}
