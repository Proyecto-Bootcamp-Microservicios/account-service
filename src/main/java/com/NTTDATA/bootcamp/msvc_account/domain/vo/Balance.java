package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Getter
public final class Balance {
    private final Currency currency;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    private Balance(Currency currency, BigDecimal amount, LocalDateTime timestamp) {
        if (currency == null) throw new IllegalArgumentException("Currency cannot be null");
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
        if (timestamp == null) throw new IllegalArgumentException("Timestamp cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Amount cannot be negative");
        this.currency = currency;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public static Balance of(Currency currency, BigDecimal amount) {
        return new Balance(currency, amount, LocalDateTime.now());
    }

    public static Balance reconstruct(Currency currency, BigDecimal amount, LocalDateTime timestamp) {
        return new Balance(currency, amount, timestamp);
    }

    public boolean hasSufficientFunds(BigDecimal amount) {
        return this.amount.compareTo(amount) >= 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

}
