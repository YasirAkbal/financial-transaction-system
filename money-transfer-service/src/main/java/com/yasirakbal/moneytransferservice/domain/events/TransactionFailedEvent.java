package com.yasirakbal.moneytransferservice.domain.events;

import com.yasirakbal.moneytransferservice.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class TransactionFailedEvent extends BaseDomainEvent {
    private final UUID transactionId;
    private final String errorCode; //INSUFFICIENT_FUNDS, ACCOUNT_BLOCKED etc.
    private final String errorMessage;
    private final String failedStep; //ACCOUNT_DEBIT, ACCOUNT_CREDIT etc.
    private final UUID sourceAccountId;
    private final UUID targetAccountId;
    private final BigDecimal amount;

    public TransactionFailedEvent(String correlationId, UUID transactionId, String errorCode, String errorMessage,
                                  String failedStep, UUID sourceAccountId, UUID targetAccountId, BigDecimal amount) {
        super(correlationId);
        this.transactionId = transactionId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.failedStep = failedStep;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
    }
}
