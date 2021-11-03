package com.weather.sensors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = "com.weather.sensors")
public class Application {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);

		Environment env = app.run(args).getEnvironment();

		System.out.println("------------------------------------------------------\n Application " +
				env.getProperty("spring.application.name") + " is running! Access URL:\n\t"
				+ "Local: \t\thttp://localhost:" + env.getProperty("server.port") + "\n------------------------------------------------------");
	}

}
