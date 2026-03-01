package com.yasirakbal.accountservice.domain.event;

import com.yasirakbal.accountservice.domain.valueobject.Money;
import com.yasirakbal.accountservice.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AccountDebitCompensatedEvent extends BaseDomainEvent {
    private final UUID compensatedAccountId;
    private final UUID relatedAccountId;
    private final UUID compensatedCustomerId;
    private final UUID relatedCustomerId;
    private final Money amount;

    public AccountDebitCompensatedEvent(String correlationId, UUID compensatedAccountId,
                                        UUID relatedAccountId, UUID compensatedCustomerId,
                                        UUID relatedCustomerId, Money amount) {
        super(correlationId);
        this.compensatedAccountId = compensatedAccountId;
        this.relatedAccountId = relatedAccountId;
        this.compensatedCustomerId = compensatedCustomerId;
        this.relatedCustomerId = relatedCustomerId;
        this.amount = amount;
    }
}