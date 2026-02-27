package com.yasirakbal.accountservice.domain.infrastructure.idempotency;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "processed_messages",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_tx_op",
                        columnNames = {"transactionId", "operationType"}
                )
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private OperationType operationType;

    private String serviceName;

    private LocalDateTime processedAt;

}

