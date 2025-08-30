package com.NTTDATA.bootcamp.msvc_account.application.dto.response;

import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountStatus;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public final class AccountResponse {
    private final String accountId;
    private final String internalAccountNumber;
    private final String externalAccountNumber;
    private final AccountType accountType;
    private final AccountStatus status;
    private final BigDecimal amount;
    private final String currency;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
