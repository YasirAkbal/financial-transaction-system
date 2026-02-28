package com.yasirakbal.moneytransferservice.domain.enums;

public enum TransferStep {
    INITIATED,
    DEBIT_SENT,
    CREDIT_SENT,
    COMPLETED,
    COMPENSATING,
    COMPENSATED
}