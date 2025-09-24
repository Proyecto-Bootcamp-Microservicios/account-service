package com.ntt.data.bootcamp.msvc.account.application.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response DTO with the current balance of an account.
 */
@Getter
@AllArgsConstructor
public class BalanceResponse {
  private final String accountId;
  private final BigDecimal balance;
  private final String currency;
}
