package com.yasirakbal.accountservice.domain.event;

import com.yasirakbal.accountservice.shared.domain.BaseDomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AccountCreatedEvent extends BaseDomainEvent {

    private final UUID accountId;

    private final UUID customerId;

    protected AccountCreatedEvent(String correlationId, UUID accountId, UUID customerId) {
        super(correlationId);
        this.accountId = accountId;
        this.customerId = customerId;
    }
}
