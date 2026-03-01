package com.yasirakbal.notificationservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    //dummy for simplicity
    @KafkaListener(topics = "transfer-events", groupId = "notification-service-group")
    public void onTransferEvent(Object event) {
        log.info("[Notification] Transfer received: {}", event);
    }
}