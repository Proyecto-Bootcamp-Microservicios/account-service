package com.ntt.data.bootcamp.msvc.account.application.dto.command;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Command to request creation of an account for a customer.
 * Example JSON:
 * {"customerId":"1","amount":1}
 */
@Getter
@AllArgsConstructor
public final class CreateAccountCommand {
  private final String customerId;
  private final BigDecimal amount;
}
