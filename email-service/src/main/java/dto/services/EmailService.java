package dto.services;

import com.rahul.email_service.configs.NotificationConfig;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService implements IEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private NotificationConfig notificationConfig;

    @Override
    public void sendEmail(String toEmail, String subject, String body) {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            String senderName = "Money Management";

            helper.setFrom(notificationConfig.getSenderEmail(), senderName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(mimeMessage);
            log.info("Email sent. Label : {}", subject);
        }catch (Exception e) {
            log.error(e.getMessage());
            return;
        }

    }
}
