package com.rahul.email_service.consumers.auth_consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IConsumers {
    void consume(ConsumerRecord<String, String> record);
    void consumeUserServiceEvents(ConsumerRecord<String, String> record);
}
