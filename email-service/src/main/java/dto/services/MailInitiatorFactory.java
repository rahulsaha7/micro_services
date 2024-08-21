package dto.services;

import com.rahul.email_service.authservices.AuthInitiator;
import com.rahul.email_service.configs.ThymeleafConfigs;
import com.rahul.email_service.userservices.UserEmailInitiator;
import dto.enums.MailServices;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class MailInitiatorFactory {

    private SpringTemplateEngine templateEngine;
    private EmailService emailService;


    public AMailInitiator getMailInitiators(MailServices service) {
        return switch (service) {
            case AUTH -> new AuthInitiator(
                templateEngine,
                emailService
            );
            case USER -> new UserEmailInitiator(
                templateEngine,
                emailService
            );
            default -> throw new RuntimeException("Something unexpeceted happended");
        };

    }

}
