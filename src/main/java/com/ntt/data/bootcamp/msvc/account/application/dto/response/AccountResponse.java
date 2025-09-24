package com.ntt.data.bootcamp.msvc.account.application.dto.response;

import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response DTO representing a bank account state for API consumers.
 */
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
