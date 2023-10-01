package com.saadahmedev.notificationservice.kafka;

import com.saadahmedev.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "user-creation-event", groupId = "notification-group")
    private void onUserCreationEventReceived(String event) {
        log.info("BEGINNING" + event);
        notificationService.onUserCreationEvent(event);
    }
}
