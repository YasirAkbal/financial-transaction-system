package com.yasirakbal.moneytransferservice.domain.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.Currency;

@Embeddable
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null.");
        if (currency == null) throw new IllegalArgumentException("Currency cannot be null.");

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }

    public Money add(Money other) {
        validateCurrencies(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        validateCurrencies(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isLessThan(Money other) {
        validateCurrencies(other);
        return this.amount().compareTo(other.amount()) < 0;
    }

    public boolean isGreaterThan(Money other) {
        validateCurrencies(other);
        return this.amount().compareTo(other.amount()) > 0;
    }

    private void validateCurrencies(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Different currencies cannot be combined.");
        }
    }
}