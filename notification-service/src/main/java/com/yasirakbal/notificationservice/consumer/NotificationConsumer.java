package com.yasirakbal.notificationservice.consumer;

import common.event.MoneyTransferCompletedIntegrationEvent;
import common.event.MoneyTransferFailedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "transfer-events", groupId = "notification-service-group")
//dummy for simplicity
public class NotificationConsumer {

    @KafkaHandler
    public void onTransferEvent(MoneyTransferCompletedIntegrationEvent event) {
        log.info("[Notification] Transfer completed notification: {}", event);
    }

    @KafkaHandler
    public void onTransferEvent(MoneyTransferFailedIntegrationEvent event) {
        log.info("[Notification] Transfer failed notification: {}", event);
    }
}