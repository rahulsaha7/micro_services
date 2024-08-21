package com.rahul.email_service.authservices;

import com.rahul.email_service.Utils.JsonUtils;
import com.rahul.email_service.authservices.models.VerifyEmailSendPojo;
import com.rahul.email_service.configs.ThymeleafConfigs;
import dto.services.AMailInitiator;
import dto.services.EmailService;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@NoArgsConstructor
public class AuthInitiator extends AMailInitiator {

    private TemplateEngine templateEngine;

    public AuthInitiator(TemplateEngine templateEngine,
        EmailService emailService) {
        super(emailService);
        this.templateEngine = templateEngine;
    }

    @Override
    public void initiateEmail(String message, String payload) {
        switch (message) {
            case "VERIFIED" :
                getVerifiedEmailDataAndSendMail(payload);
                break;
            case "FORGOT-PASSWORD" :

                break;
            case "VERIFY-EMAIL":
                getVerifyEmailDataAndSendMail(payload); // This will change now
                break;
            case "REGISTRATION":
                getRegistrationEmailDataAndSendMail(payload);
                break;
            default:
                throw new RuntimeException("something unexpected happened");
        }
    }

    private void getRegistrationEmailDataAndSendMail(String payload) {
        VerifyEmailSendPojo payloadInPojo = JsonUtils.getJsonObject(payload, VerifyEmailSendPojo.class);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("username", payloadInPojo.getPayload().getUsername());
        templateModel.put("token", payloadInPojo.getPayload().getPayload());
        templateModel.put("userId", payloadInPojo.getSenderEmailId());



        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = templateEngine.process("authtemplates/RegistrationEmailTemplate", thymeleafContext);

        sendEmail(payloadInPojo.getSenderEmailId(), "Registration Completion", htmlBody);
    }

    private void getVerifyEmailDataAndSendMail(String payload) {

        VerifyEmailSendPojo payloadInPojo = JsonUtils.getJsonObject(payload, VerifyEmailSendPojo.class);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("username", payloadInPojo.getPayload().getUsername());
        templateModel.put("token", payloadInPojo.getPayload().getPayload());
        System.out.println("Toekn" + payloadInPojo.getPayload().getPayload());


        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = templateEngine.process("authtemplates/VerifyEmailTemplate", thymeleafContext);

        sendEmail(payloadInPojo.getSenderEmailId(), "Email Verification", htmlBody);
    }

    private void getVerifiedEmailDataAndSendMail(String payload) {

        VerifyEmailSendPojo payloadInPojo = JsonUtils.getJsonObject(payload, VerifyEmailSendPojo.class);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("username", payloadInPojo.getPayload().getUsername());
        templateModel.put("userId", payloadInPojo.getSenderEmailId());


        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = templateEngine.process("authtemplates/VerifiedEmailTemplate", thymeleafContext);

        sendEmail(payloadInPojo.getSenderEmailId(), "Email Verified", htmlBody);


    }
}
