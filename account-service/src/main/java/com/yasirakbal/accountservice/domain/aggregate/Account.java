package com.yasirakbal.accountservice.domain.aggregate;

import com.yasirakbal.accountservice.domain.event.AccountCreatedEvent;
import com.yasirakbal.accountservice.domain.event.AccountCreditedEvent;
import com.yasirakbal.accountservice.domain.event.AccountDebitCompensatedEvent;
import com.yasirakbal.accountservice.domain.event.AccountDebitedEvent;
import com.yasirakbal.accountservice.domain.valueobject.Money;
import com.yasirakbal.accountservice.shared.domain.BaseAggregateRoot;
import com.yasirakbal.accountservice.shared.util.AccountNumberGenerator;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Getter
@Entity
@Table(name = "accounts")
public class Account extends BaseAggregateRoot<Account> {
    private Long accountNumber;

    private UUID customerId;

    @Embedded
    private Money balance;

    protected Account() { }


    public static Account create(UUID customerId, Long accountNumber, Currency currency, BigDecimal initialBalance, String correlationId) {
        if (accountNumber == null || accountNumber <= 0) {
            throw new IllegalArgumentException("Invalid account number");
        }

        Account account = new Account();
        account.accountNumber = accountNumber;
        account.customerId = customerId;
        account.balance = new Money(initialBalance, currency);

        account.registerEvent(new AccountCreatedEvent(
                correlationId,
                account.getId(),
                account.getCustomerId()
        ));

        return account;
    }

    public void creditAccount(UUID sourceAccountId, UUID sourceCustomerId, Money amount, String correlationId) {
        this.balance = this.balance.add(amount);

        registerEvent(new AccountCreditedEvent(
                correlationId,
                this.getId(),
                sourceAccountId,
                this.getCustomerId(),
                sourceCustomerId,
                this.balance
        ));
    }

    public void debitAccount(UUID targetAccountId, UUID targetCustomerId, Money amount, String correlationId) {
        if (this.balance.isLessThan(amount)) {
            throw new IllegalArgumentException("Not enough funds");
        }

        this.balance = this.balance.subtract(amount);

        registerEvent(new AccountDebitedEvent(
                correlationId,
                this.getId(),
                targetAccountId,
                this.getCustomerId(),
                targetCustomerId,
                this.balance
        ));
    }

    public void compensateDebit(UUID targetAccountId, UUID targetCustomerId,
                                Money amount, String correlationId) {
        this.balance = this.balance.add(amount);

        registerEvent(new AccountDebitCompensatedEvent(
                correlationId,
                this.getId(),
                targetAccountId,
                this.getCustomerId(),
                targetCustomerId,
                amount
        ));
    }
}
