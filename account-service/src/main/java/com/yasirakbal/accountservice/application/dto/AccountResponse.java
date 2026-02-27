package com.yasirakbal.accountservice.application.dto;

import com.yasirakbal.accountservice.domain.aggregate.Account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        Long accountNumber,
        UUID customerId,
        BigDecimal balance,
        String currency
) {
    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getCustomerId(),
                account.getBalance().amount(),
                account.getBalance().currency().toString()
        );
    }
}