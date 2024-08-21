package com.rahul.user_service.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IConsumer {
    void consumeAuthServiceEvents(ConsumerRecord<String, String> record);

}
