package com.yasirakbal.moneytransferservice.domain.events;

import com.yasirakbal.moneytransferservice.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class TransactionInitiatedEvent extends BaseDomainEvent {
    private final UUID transactionId;
    private final UUID sourceAccountId;
    private final UUID targetAccountId;
    private final BigDecimal amount;
    private final String currency;

    public TransactionInitiatedEvent(String correlationId, UUID transactionId, UUID sourceAccountId, UUID targetAccountId, BigDecimal amount, String currency) {
        super(correlationId);
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.currency = currency;
    }
}
