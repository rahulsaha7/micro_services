package com.rahul.auth_service;

import com.rahul.auth_service.configs.ApplicationConfigs;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@SpringBootApplication
@EnableFeignClients
public class AuthServiceApplication {
	public static final String LOGFILE_DIRECTORY = "logfile.directory";
	public static void main(String[] args) {
		System.setProperty("logfile.directory", System.getProperty("user.home") + "/Rahul/micro-service-logs");
		System.setProperty("logfile.name", System.getProperty(LOGFILE_DIRECTORY) + "/auth-service.log");
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		String configPath = "classpath:" + applicationContext.getEnvironment().getActiveProfiles()[0] + "/log4j2.properties";
		Configurator.initialize(null, configPath);
		SpringApplication.run(AuthServiceApplication.class, args
		);
	}

}
