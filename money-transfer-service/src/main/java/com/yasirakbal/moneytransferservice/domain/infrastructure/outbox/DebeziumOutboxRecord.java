package com.yasirakbal.moneytransferservice.domain.infrastructure.outbox;

public record DebeziumOutboxRecord(String id, String topic, String aggregateId,
                                   String eventType, String payload) {}