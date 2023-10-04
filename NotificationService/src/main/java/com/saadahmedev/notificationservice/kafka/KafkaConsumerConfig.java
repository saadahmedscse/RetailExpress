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
        notificationService.onUserCreationEvent(event);
    }

    @KafkaListener(topics = "amount-deposit-event", groupId = "notification-group")
    private void onDepositEvent(String event) {
        notificationService.onDepositEvent(event);
    }
}
