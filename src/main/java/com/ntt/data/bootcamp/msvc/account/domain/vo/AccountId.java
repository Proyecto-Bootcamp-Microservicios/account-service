package com.ntt.data.bootcamp.msvc.account.domain.vo;

import static com.ntt.data.bootcamp.msvc.account.domain.util.DomainUtils.*;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "value")
public final class AccountId {
  private final String value;

  private AccountId(String value) {
    reformat(List.of(value));
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Customer ID cannot be null or empty");
    if (!isValidUUID(value)) throw new IllegalArgumentException("Customer ID must be a valid UUID");
    this.value = value;
  }

  public static AccountId of(String value) {
    return new AccountId(value);
  }
}
