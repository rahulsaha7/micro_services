package com.rahul.email_service.consumers.auth_consumer;

import dto.enums.MailServices;
import dto.services.EmailService;
import dto.services.MailInitiatorFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class Consumers implements IConsumers {

    private MailInitiatorFactory mailInitiatorFactory;

    @Override
    @KafkaListener(topics = "AUTH_EMAILS", groupId = "email-group")
    public void consume(ConsumerRecord<String, String> record) {
        String key = record.key();
        if(StringUtils.isBlank(key)) {
            throw new RuntimeException("Something unexpected happen");
        }
        initiateMail(MailServices.AUTH, record.key(), record.value());
    }

    @Override
    @KafkaListener(topics = "USER_EMAILS", groupId = "email-group")
    public void consumeUserServiceEvents(ConsumerRecord<String, String> record) {
        String key = record.key();
        if(StringUtils.isBlank(key)) {
            throw new RuntimeException("Something unexpected happen");
        }
        initiateMail(MailServices.USER, record.key(), record.value());
    }

    private void initiateMail(MailServices mailService, String key, String value) {
        mailInitiatorFactory.getMailInitiators(mailService).initiateEmail(key, value);
    }
}
