package dto.services;

public interface IEmailService {
    void sendEmail(String toEmail, String subject, String body);

}
