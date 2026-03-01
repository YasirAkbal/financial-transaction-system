package com.yasirakbal.moneytransferservice.domain.aggregate;

import com.yasirakbal.moneytransferservice.domain.enums.TransferStatus;
import com.yasirakbal.moneytransferservice.domain.enums.TransferStep;
import com.yasirakbal.moneytransferservice.domain.events.TransactionCompletedEvent;
import com.yasirakbal.moneytransferservice.domain.events.TransactionFailedEvent;
import com.yasirakbal.moneytransferservice.domain.events.TransactionInitiatedEvent;
import com.yasirakbal.moneytransferservice.domain.valueobject.Money;
import com.yasirakbal.moneytransferservice.shared.domain.BaseAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "transfer_transactions")
public class Transaction extends BaseAggregateRoot<Transaction> {

    private UUID sourceAccountId;
    private UUID sourceCustomerId;
    private UUID targetAccountId;
    private UUID targetCustomerId;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    private TransferStep currentStep;

    @Column(nullable = false, unique = true)
    private String correlationId;

    protected Transaction() {}

    public static Transaction create(
            UUID sourceAccountId, UUID sourceCustomerId,
            UUID targetAccountId, UUID targetCustomerId,
            Money amount, String correlationId) {

        Transaction transaction = new Transaction();
        transaction.sourceAccountId = sourceAccountId;
        transaction.sourceCustomerId = sourceCustomerId;
        transaction.targetAccountId = targetAccountId;
        transaction.targetCustomerId = targetCustomerId;
        transaction.amount = amount;
        transaction.status = TransferStatus.PENDING;
        transaction.currentStep = TransferStep.INITIATED;
        transaction.initiatedAt = LocalDateTime.now();
        transaction.correlationId = correlationId;

        transaction.registerEvent(new TransactionInitiatedEvent(
                correlationId,
                transaction.getId(),
                sourceAccountId,
                sourceCustomerId,
                targetAccountId,
                targetCustomerId,
                amount.amount(),
                amount.currency().toString()
        ));

        return transaction;
    }

    public void markDebitSent() {
        this.currentStep = TransferStep.DEBIT_SENT;
    }

    public void markCreditSent() {
        this.currentStep = TransferStep.CREDIT_SENT;
    }

    public void markCompensated() {
        this.currentStep = TransferStep.COMPENSATED;
    }

    public void complete(String correlationId) {
        this.status = TransferStatus.COMPLETED;
        this.currentStep = TransferStep.COMPLETED;
        this.completedAt = LocalDateTime.now();

        this.registerEvent(new TransactionCompletedEvent(
                correlationId, this.getId(),
                this.sourceAccountId, this.targetAccountId,
                this.amount.amount(), this.amount.currency().toString()
        ));
    }

    public void fail(String errorCode, String errorMessage,
                     String failedStep, String correlationId) {
        this.status = TransferStatus.FAILED;
        this.completedAt = LocalDateTime.now();

        this.registerEvent(new TransactionFailedEvent(
                correlationId, this.getId(), errorCode,
                errorMessage, failedStep,
                this.sourceAccountId, this.targetAccountId,
                this.amount.amount()
        ));
    }
}
