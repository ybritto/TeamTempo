package com.ybritto.teamtempo.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TeamTempoApplication {

	private static final Logger logger = LoggerFactory.getLogger(TeamTempoApplication.class);

	public static void main(String[] args) {
		logger.info("Starting TeamTempo Backend Application...");

		ConfigurableApplicationContext context = SpringApplication.run(TeamTempoApplication.class, args);

		logger.info("TeamTempo Backend Application started successfully!");
		logger.info("Application name: {}", context.getEnvironment().getProperty("spring.application.name"));
		logger.info("Active profiles: {}", String.join(", ", context.getEnvironment().getActiveProfiles()));
		logger.info("Server port: {}", context.getEnvironment().getProperty("server.port", "8080"));
	}



}
