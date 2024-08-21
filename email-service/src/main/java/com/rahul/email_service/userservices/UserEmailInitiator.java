package com.rahul.email_service.userservices;

import dto.services.AMailInitiator;
import dto.services.EmailService;
import org.thymeleaf.TemplateEngine;

public class UserEmailInitiator extends AMailInitiator {

    private TemplateEngine templateEngine;

    public UserEmailInitiator(TemplateEngine templateEngine, EmailService emailService) {
        super(emailService);
        this.templateEngine = templateEngine;
    }

    @Override
    public void initiateEmail(String message, String payload) {
            switch (message) {
                case "Testing" :
                    break;
            }
    }
}
