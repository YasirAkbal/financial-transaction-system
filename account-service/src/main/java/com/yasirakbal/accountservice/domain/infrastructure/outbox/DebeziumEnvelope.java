package com.yasirakbal.accountservice.shared.dto;

public record DebeziumEnvelope(String opType, DebeziumOutboxRecord record) {}