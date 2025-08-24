package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import lombok.Getter;

import java.util.List;

import static com.NTTDATA.bootcamp.msvc_account.domain.util.DomainUtils.*;

@Getter
public final class ExternalAccountNumber {
    private final String value;

    private ExternalAccountNumber(String value) {
        reformat(List.of(value));
        if (value == null || value.isEmpty()) throw new IllegalArgumentException("ExternalAccountNumber cannot be null or blank");
        if (!isValidExternalAccountNumber(value)) throw new IllegalArgumentException("ExternalAccountNumber must be 24 characters long");
        this.value = value;
    }

    public static ExternalAccountNumber of(String value) {
        return new ExternalAccountNumber(value);
    }

    private boolean isValidExternalAccountNumber(String value) {
        return value.matches("^\\d{20}$");
    }
}
