package com.rahul.user_service.consumers;

import com.rahul.user_service.service.IUserService;
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
public class Consumer implements IConsumer{

    private IUserService userService;

    @Override
    @KafkaListener(topics = "USERS", groupId = "auth-group")
    public void consumeAuthServiceEvents(ConsumerRecord<String, String> record) {
        String key = record.key();
        if(StringUtils.isBlank(key)) {
            throw new RuntimeException("Something unexpected happen");
        }
        log.info("testing, key: {}", key);
        userService.registerUser(record.value());
    }
}
