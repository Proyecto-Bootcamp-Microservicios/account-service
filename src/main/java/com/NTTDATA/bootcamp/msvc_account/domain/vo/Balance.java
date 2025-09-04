package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@EqualsAndHashCode(of = {"currency", "amount", "timestamp"})
@Getter
public final class Balance {
    private final Currency currency;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    private Balance(String currency, BigDecimal amount, LocalDateTime timestamp) {
        if (currency == null) throw new IllegalArgumentException("Currency cannot be null");
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
        if (timestamp == null) throw new IllegalArgumentException("Timestamp cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Amount cannot be negative");
        this.currency = getCurrencyInstance(currency);
        this.amount = amount;
        this.timestamp = timestamp;
    }

    private Currency getCurrencyInstance(String currency) {
        try{
            return Currency.getInstance(currency);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid currency");
        }
    }

    public static Balance of(String currency, BigDecimal amount) {
        return new Balance(currency, amount, LocalDateTime.now());
    }

    public static Balance zero(String currency) {
        return new Balance(currency, BigDecimal.ZERO, LocalDateTime.now());
    }

    public static Balance reconstruct(String currency, BigDecimal amount, LocalDateTime timestamp) {
        return new Balance(currency, amount, timestamp);
    }

    public Balance update(BigDecimal amount) {
        BigDecimal newAmount = this.amount.add(amount);
        return new Balance(this.currency.getCurrencyCode(), newAmount, LocalDateTime.now());
    }



    public boolean isGreaterThan(Balance other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare balances in different currencies");
        }
        return amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Balance other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare balances in different currencies");
        }
        return amount.compareTo(other.amount) < 0;
    }



    public boolean isRecent() {
        return LocalDateTime.now().minusHours(1).isBefore(timestamp);
    }

    public boolean isStale() {
        return LocalDateTime.now().minusDays(1).isAfter(timestamp);
    }

}
