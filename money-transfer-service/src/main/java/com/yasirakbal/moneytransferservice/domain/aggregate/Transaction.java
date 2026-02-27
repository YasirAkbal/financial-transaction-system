package com.yasirakbal.moneytransferservice.domain.aggregate;

import com.yasirakbal.moneytransferservice.domain.enums.TransferStatus;
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

    private UUID targetAccountId ;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    private LocalDateTime initiatedAt;

    private LocalDateTime completedAt;

    protected Transaction() { }

    public static Transaction create(UUID sourceAccountId, UUID targetAccountId, Money amount, String correlationId) {
        Transaction transaction = new Transaction();
        transaction.sourceAccountId = sourceAccountId;
        transaction.targetAccountId = targetAccountId;
        transaction.amount = amount;
        transaction.status = TransferStatus.PENDING;
        transaction.initiatedAt = LocalDateTime.now();

        transaction.registerEvent(new TransactionInitiatedEvent(
                correlationId,
                transaction.getId(),
                transaction.getSourceAccountId(),
                transaction.getTargetAccountId(),
                transaction.getAmount().amount(),
                transaction.getAmount().currency().toString()
        ));

        return transaction;
    }

    public void complete(String correlationId) {
        this.status = TransferStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();

        this.registerEvent(new TransactionCompletedEvent(
                correlationId,
                this.getId(),
                this.getSourceAccountId(),
                this.getTargetAccountId(),
                this.getAmount().amount(),
                this.getAmount().currency().toString()
        ));
    }

    public void fail(String errorCode, String errorMessage, String failedStep, String correlationId) {
        this.status = TransferStatus.FAILED;
        this.completedAt = LocalDateTime.now();

        this.registerEvent(new TransactionFailedEvent(
                correlationId,
                this.getId(),
                errorCode,
                errorMessage,
                failedStep,
                this.getSourceAccountId(),
                this.getTargetAccountId(),
                this.getAmount().amount()
        ));
    }

}
