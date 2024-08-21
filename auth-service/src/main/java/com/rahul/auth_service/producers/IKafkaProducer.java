package com.rahul.auth_service.producers;

public interface IKafkaProducer {
    void emitUserRegistrationEvent(String message, String payload);
    void emitEmailInitiationEvent(String message, String payload);
}
