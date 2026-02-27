package com.yasirakbal.moneytransferservice.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransferStatus {
    PENDING("Pending"), COMPLETED("Completed"), FAILED("Failed");

    private final String displayName;
}
