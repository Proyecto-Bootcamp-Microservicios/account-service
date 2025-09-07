package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BalanceCollection {
  private BigDecimal amount;
  private String currencyCode;
  private LocalDateTime timestamp;
}
