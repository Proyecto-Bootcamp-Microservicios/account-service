package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import com.NTTDATA.bootcamp.msvc_account.domain.util.AccountNumberGenerator;
import lombok.Getter;

import java.util.List;

import static com.NTTDATA.bootcamp.msvc_account.domain.util.DomainUtils.*;

@Getter
public final class InternalAccountNumber {
    private final String value;

    private InternalAccountNumber(String value) {
        reformat(List.of(value));
        if (value == null || value.isEmpty()) throw new IllegalArgumentException("InternalAccountNumber cannot be null or blank");
        if (!isValidInternalAccountNumber(value)) throw new IllegalArgumentException("InternalAccountNumber must be 14 digits");
        this.value = value;
    }

    public static InternalAccountNumber of() {
        return new InternalAccountNumber(AccountNumberGenerator.generateInternal());
    }

    public static InternalAccountNumber of(String value) {
        return new InternalAccountNumber(value);
    }

    private boolean isValidInternalAccountNumber(String value) {
        return value.matches("^\\d{14}$");
    }

}
