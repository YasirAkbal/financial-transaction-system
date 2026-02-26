package com.yasirakbal.accountservice.domain.event;

import com.yasirakbal.accountservice.domain.valueobject.Money;
import com.yasirakbal.accountservice.shared.domain.BaseDomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AccountClosedEvent extends BaseDomainEvent {

    private final UUID accountId;

    private final UUID customerId;

    private final String closureReason;

    private final Money finalBalance;


    protected AccountClosedEvent(String correlationId, UUID accountId, UUID customerId, String closureReason, Money finalBalance) {
        super(correlationId);
        this.accountId = accountId;
        this.customerId = customerId;
        this.closureReason = closureReason;
        this.finalBalance = finalBalance;
    }
}
