package com.rahul.auth_service.producers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class KafkaProducer implements IKafkaProducer{

    private static final String  USERS_TOPIC = "USERS";
    private static final String  EMAIL_TOPIC = "AUTH_EMAILS";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void emitUserRegistrationEvent(String message,String payload) {
        kafkaTemplate.send(USERS_TOPIC, message, payload);
    }

    @Override
    public void emitEmailInitiationEvent(String message, String payload) {
        System.out.println("testing this out");
        kafkaTemplate.send(EMAIL_TOPIC, message, payload);
    }
}
