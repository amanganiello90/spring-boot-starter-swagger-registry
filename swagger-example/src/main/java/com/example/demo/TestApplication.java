package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import org.springframework.context.event.EventListener;

@SpringBootApplication

public class TestApplication {

	private static final Logger logger = LoggerFactory.getLogger(TestApplication.class);

	// value inserted in your application.properties server.port
	@Value("${server.port}")
	private String port;

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);

	}

	@EventListener(ApplicationReadyEvent.class)
	public void postConstruct() {
		logger.info("-----OPEN BROWSER IN localhost:" + port);
	}

}
