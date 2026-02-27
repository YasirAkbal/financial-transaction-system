package com.yasirakbal.accountservice.domain.infrastructure.idempotency;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OperationType {
    DEBIT("Debit"), CREDIT("Credit"), CREATE("Create");

    private final String displayName;
}
