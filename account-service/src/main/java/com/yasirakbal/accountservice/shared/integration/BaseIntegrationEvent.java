package com.yasirakbal.accountservice.shared.integration;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseIntegrationEvent {
    private final UUID id;
    private final LocalDateTime occurredOn;
    private final String correlationId;
    private final String eventType;

    protected BaseIntegrationEvent(String correlationId) {
        this.id = UUID.randomUUID();
        this.occurredOn = LocalDateTime.now();
        this.correlationId = correlationId;
        this.eventType = this.getClass().getSimpleName();
    }


}