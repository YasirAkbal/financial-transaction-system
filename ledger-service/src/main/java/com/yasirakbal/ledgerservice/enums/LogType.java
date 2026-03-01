package com.yasirakbal.ledgerservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogType {
    MONEY_TRANSFER_COMPLETED("TRANSFER_COMPLETED"), MONEY_TRANSFER_FAILED("TRANSFER_FALIED");

    private final String stringValue;
}
