package com.yasirakbal.accountservice.domain.infrastructure.outbox;

public record DebeziumEnvelope(String opType, DebeziumOutboxRecord record) {}