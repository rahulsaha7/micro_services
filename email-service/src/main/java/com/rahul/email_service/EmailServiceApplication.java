package com.rahul.email_service;

import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@SpringBootApplication
@ComponentScan(basePackages = "*")
public class EmailServiceApplication {

	public static void main(String[] args) {
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		String configPath = "classpath:" + applicationContext.getEnvironment().getActiveProfiles()[0] + "/log4j2.properties";
		Configurator.initialize(null, configPath);
		SpringApplication.run(EmailServiceApplication.class, args);
	}

}
