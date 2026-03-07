package com.yasirakbal.accountservice.domain.infrastructure.outbox;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DebeziumOpType {
    CREATE("c"),
    UPDATE("u"),
    DELETE("d");

    private final String opType;
}
