package com.rahul.auth_service.configs;

import java.io.IOException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbConfigs.getDatabaseUrl());
        dataSource.setUsername(dbConfigs.getDatabaseUsername());
        dataSource.setPassword(dbConfigs.getDatabasePassword());
        dataSource.setDriverClassName(dbConfigs.getDatabaseDriver());
        return dataSource;
    }


    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer(ApplicationContext applicationContext)
        throws IOException {

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
        return propertyConfigurator;
    }

}
