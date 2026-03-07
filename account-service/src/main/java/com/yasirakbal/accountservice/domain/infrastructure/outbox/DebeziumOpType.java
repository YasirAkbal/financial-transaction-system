package com.yasirakbal.accountservice.shared.enums;

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
