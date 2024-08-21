package com.rahul.eureka_server.configs;

import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@Slf4j
public class ApplicationConfigs {


    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer(ApplicationContext applicationContext)
        throws IOException {

        ConfigurableEnvironment environment = (ConfigurableEnvironment) applicationContext.getEnvironment();

        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        if (activeProfiles.length != 1) {
            throw new IOException("No active profiles");
        }
        PropertySourcesPlaceholderConfigurer propertyConfigurator = new PropertySourcesPlaceholderConfigurer();
        Resource[] projectResources = ArrayUtils.addAll(
            new PathMatchingResourcePatternResolver().getResources("classpath:/common/**/*.properties"),
            new PathMatchingResourcePatternResolver().getResources(
                "classpath:" + activeProfiles[0] + "/**/*.properties"));
        propertyConfigurator.setLocations(projectResources);

        Properties properties = new Properties();
        for (Resource resource : projectResources) {
            properties.load(resource.getInputStream());
        }

        environment.getPropertySources().addFirst(new PropertiesPropertySource("dynamicProperties", properties));


        return propertyConfigurator;
    }

}
