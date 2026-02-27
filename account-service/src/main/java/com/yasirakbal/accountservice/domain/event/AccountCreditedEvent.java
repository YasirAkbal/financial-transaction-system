package com.yasirakbal.accountservice.domain.event;

import com.yasirakbal.accountservice.domain.valueobject.Money;
import com.yasirakbal.accountservice.shared.domain.BaseDomainEvent;
import lombok.Getter;
import java.util.UUID;

@Getter
public class AccountCreditedEvent extends BaseDomainEvent {

    private final UUID creditedAccountId;

    private final UUID debitedAccountId;

    private final UUID creditedCustomerId;

    private final UUID debitedCustomerId;

    private final Money amount;

    public AccountCreditedEvent(String correlationId, UUID creditedAccountId, UUID debitedAccountId, UUID creditedCustomerId, UUID debitedCustomerId, Money amount) {
        super(correlationId);
        this.creditedAccountId = creditedAccountId;
        this.debitedAccountId = debitedAccountId;
        this.creditedCustomerId = creditedCustomerId;
        this.debitedCustomerId = debitedCustomerId;
        this.amount = amount;
    }
}
