package com.rahul.auth_service.configs;

import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@Slf4j
public class ApplicationConfigs {

    @Autowired
    private DbConfigs dbConfigs;

    @Bean
    public DataSource dataSource() {
        log.info(log.getClass().getName());
        log.info("Creating datasource");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbConfigs.getDatabaseUrl());
        dataSource.setUsername(dbConfigs.getDatabaseUsername());
        dataSource.setPassword(dbConfigs.getDatabasePassword());
        dataSource.setDriverClassName(dbConfigs.getDatabaseDriver());
        System.out.println("Datasource created , url : {} " + dbConfigs.getDatabaseUrl());
        return dataSource;
    }


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
            log.info("Loading properties from {}", resource.getFilename());
            properties.load(resource.getInputStream());
        }

        environment.getPropertySources().addFirst(new PropertiesPropertySource("dynamicProperties", properties));


        return propertyConfigurator;
    }

}
