package com.yasirakbal.ledgerservice.dto;

import com.yasirakbal.ledgerservice.entity.Ledger;
import com.yasirakbal.ledgerservice.enums.LogType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LedgerResponse(
        Long id,
        String corrId,
        LogType logType,
        BigDecimal amount,
        String currency,
        LocalDateTime occurredOn,
        UUID sourceAccountId,
        UUID targetAccountId,
        String errorCode,
        String errorMessage,
        String failedStep,
        LocalDateTime createdAt
) {
    public static LedgerResponse from(Ledger ledger) {
        return new LedgerResponse(
                ledger.getId(),
                ledger.getCorrId(),
                ledger.getLogType(),
                ledger.getAmount(),
                ledger.getCurrency(),
                ledger.getOccurredOn(),
                ledger.getSourceAccountId(),
                ledger.getTargetAccountId(),
                ledger.getErrorCode(),
                ledger.getErrorMessage(),
                ledger.getFailedStep(),
                ledger.getCreatedAt()
        );
    }
}