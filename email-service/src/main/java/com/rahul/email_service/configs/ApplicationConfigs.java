package com.rahul.email_service.configs;

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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
@Slf4j
public class ApplicationConfigs {

    @Autowired
    private NotificationConfig notificationConfig;

    @Autowired
    private ThymeleafConfigs thymeleafConfigs;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(notificationConfig.getEmailHost());
        mailSender.setPort(notificationConfig.getEmailPort());
        mailSender.setUsername(notificationConfig.getEmailUsername());
        mailSender.setPassword(notificationConfig.getEmailPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", notificationConfig.getEmailDebug());
        return mailSender;
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


    @Bean
    public ClassLoaderTemplateResolver secondaryTemplateResolver() {
        ClassLoaderTemplateResolver secondaryTemplateResolver = new ClassLoaderTemplateResolver();
        secondaryTemplateResolver.setPrefix(thymeleafConfigs.getAuthTemplatesLocation());
        secondaryTemplateResolver.setSuffix(thymeleafConfigs.getTemplateSuffix());
        secondaryTemplateResolver.setTemplateMode(TemplateMode.HTML);
        secondaryTemplateResolver.setCharacterEncoding("UTF-8");
        secondaryTemplateResolver.setOrder(1);
        secondaryTemplateResolver.setCheckExistence(true);
        return secondaryTemplateResolver;
    }

}
