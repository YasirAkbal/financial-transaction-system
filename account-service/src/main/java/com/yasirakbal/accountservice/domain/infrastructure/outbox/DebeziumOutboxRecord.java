package com.yasirakbal.accountservice.shared.dto;

public record DebeziumOutboxRecord(String id, String topic, String aggregateId,
                                   String eventType, String payload) {}