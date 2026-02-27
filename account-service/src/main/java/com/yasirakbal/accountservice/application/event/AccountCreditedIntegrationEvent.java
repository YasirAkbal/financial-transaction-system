package com.yasirakbal.accountservice.application.event;

import com.yasirakbal.accountservice.shared.integration.BaseIntegrationEvent;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Getter
public class AccountCreditedIntegrationEvent extends BaseIntegrationEvent {
    private final UUID creditedAccountId;

    private final UUID debitedAccountId;

    private final UUID creditedCustomerId;

    private final UUID debitedCustomerId;

    private final BigDecimal amount;

    private final Currency currency;


    public AccountCreditedIntegrationEvent(UUID creditedAccountId, UUID debitedAccountId, UUID creditedCustomerId,
                                           UUID debitedCustomerId, BigDecimal amount, Currency currency, String correlationId) {
        super(correlationId);
        this.creditedAccountId = creditedAccountId;
        this.debitedAccountId = debitedAccountId;
        this.creditedCustomerId = creditedCustomerId;
        this.debitedCustomerId = debitedCustomerId;
        this.amount = amount;
        this.currency = currency;
    }
}
