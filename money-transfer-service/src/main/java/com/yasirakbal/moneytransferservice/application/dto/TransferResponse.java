package com.yasirakbal.moneytransferservice.application.dto;

import com.yasirakbal.moneytransferservice.domain.aggregate.Transaction;
import com.yasirakbal.moneytransferservice.domain.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        TransferStatus status,
        BigDecimal amount,
        String currency,
        LocalDateTime initiatedAt
) {
    public static TransferResponse fromEntity(Transaction tx) {
        return new TransferResponse(
                tx.getId(), tx.getStatus(),
                tx.getAmount().amount(),
                tx.getAmount().currency().toString(),
                tx.getInitiatedAt()
        );
    }
}