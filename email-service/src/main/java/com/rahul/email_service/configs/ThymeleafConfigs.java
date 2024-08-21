package com.rahul.email_service.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
@Getter
@Service
public class ThymeleafConfigs {

    @Value("${template.auth.prefix}")
    private String authTemplatesLocation;

    @Value("${template.suffix}")
    private String templateSuffix;

    @Value("${template.mode}")
    private String templateMode;


}
