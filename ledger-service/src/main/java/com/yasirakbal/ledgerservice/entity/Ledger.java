package com.yasirakbal.ledgerservice.entity;

import com.yasirakbal.ledgerservice.common.entity.BaseEntity;
import com.yasirakbal.ledgerservice.enums.LogType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_logs", indexes = {
        @Index(name = "idx_id", columnList = "id"),
        @Index(name = "idx_corrId", columnList = "corrId"),
        @Index(name = "idx_sourceAccountId", columnList = "sourceAccountId"),
        @Index(name = "idx_targetAccountId", columnList = "targetAccountId")
})
public class Ledger extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private String corrId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private LogType logType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private LocalDateTime occurredOn;

    @Column(nullable = false)
    private UUID sourceAccountId;

    @Column(nullable = false)
    private UUID targetAccountId;

    @Column(nullable = true)
    private String errorCode; //INSUFFICIENT_FUNDS, ACCOUNT_BLOCKED etc.

    @Column(nullable = true)
    private String errorMessage;

    @Column(nullable = true)
    private String failedStep; //ACCOUNT_DEBIT, ACCOUNT_CREDIT etc.
}
