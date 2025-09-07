package com.ntt.data.bootcamp.msvc.account.domain.vo;

import static com.ntt.data.bootcamp.msvc.account.domain.util.DomainUtils.*;

import com.ntt.data.bootcamp.msvc.account.domain.util.AccountNumberGenerator;
import java.util.List;
import lombok.Getter;

@Getter
public final class InternalAccountNumber {
  private final String value;

  private InternalAccountNumber(String value) {
    reformat(List.of(value));
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("InternalAccountNumber cannot be null or blank");
    if (!isValidInternalAccountNumber(value))
      throw new IllegalArgumentException("InternalAccountNumber must be 14 digits");
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
