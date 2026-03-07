package com.yasirakbal.moneytransferservice.domain.infrastructure.outbox;

public record DebeziumEnvelope(String opType, DebeziumOutboxRecord record) {}