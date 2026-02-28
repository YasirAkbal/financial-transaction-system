package com.yasirakbal.moneytransferservice.domain.enums;

public enum TransferStep {
    INITIATED,
    DEBIT_SENT,
    DEBIT_COMPLETED,
    CREDIT_SENT,
    COMPLETED,
    COMPENSATING,
    COMPENSATED
}