package dto.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
@AllArgsConstructor(onConstructor_ = {@Autowired})
public abstract class AMailInitiator {

    private EmailService emailService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public abstract void initiateEmail(String message, String payload);

    public void sendEmail(String to, String subject, String body) {
        executorService.submit(() -> {
            try {
                emailService.sendEmail(to, subject, body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

}
