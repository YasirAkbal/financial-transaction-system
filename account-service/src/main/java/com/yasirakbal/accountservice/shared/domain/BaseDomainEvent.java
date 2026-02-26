package com.yasirakbal.accountservice.shared.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class BaseDomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredOn;
    private final String correlationId;

    protected BaseDomainEvent(String correlationId) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = LocalDateTime.now();
        this.correlationId = (correlationId != null && !correlationId.isEmpty())
                ? correlationId
                : "internal-" + UUID.randomUUID();
    }

}