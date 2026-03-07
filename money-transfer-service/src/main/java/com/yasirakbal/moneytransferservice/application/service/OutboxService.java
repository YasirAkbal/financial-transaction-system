package com.yasirakbal.moneytransferservice.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.yasirakbal.moneytransferservice.domain.infrastructure.outbox.OutboxMessage;
import com.yasirakbal.moneytransferservice.domain.infrastructure.outbox.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxMessageRepository outboxMessageRepository;
    private final ObjectMapper objectMapper;

    public void saveOutbox(String topic, String aggregateId, String eventType, Object event) {
        try {
            outboxMessageRepository.save(OutboxMessage.builder()
                    .topic(topic)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(objectMapper.writeValueAsString(event))
                    .build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize outbox event", e);
        }
    }
}
