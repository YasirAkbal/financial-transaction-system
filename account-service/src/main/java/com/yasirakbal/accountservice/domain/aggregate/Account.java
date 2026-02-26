package com.yasirakbal.accountservice.domain.aggregate;

import com.yasirakbal.accountservice.domain.event.BalanceCreditedEvent;
import com.yasirakbal.accountservice.domain.valueobject.Money;
import com.yasirakbal.accountservice.shared.constant.GeneralConstants;
import com.yasirakbal.accountservice.shared.domain.BaseAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import org.slf4j.MDC;

import java.util.UUID;

@Getter
@Entity
@Table(name = "accounts")
public class Account extends BaseAggregateRoot<Account> {
    private Long accountNumber;

    private UUID customerId;

    private Money balance;

    public void creditAccount(Account sourceAccount, Money amount, String correlationId) {
        if(sourceAccount == null || sourceAccount.getId() == null) {
            throw new IllegalArgumentException("Source account cannot be null");
        }

        this.balance = this.balance.add(amount);

        registerEvent(new BalanceCreditedEvent(
                correlationId,
                this.getId(),
                sourceAccount.getId(),
                this.getCustomerId(),
                sourceAccount.getCustomerId(),
                balance
        ));
    }

}
