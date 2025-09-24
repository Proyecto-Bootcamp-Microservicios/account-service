package com.ntt.data.bootcamp.msvc.account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyBalanceSnapshotResponse {
  private String id;
  private String accountId;
  private String customerId;
  private String accountType;
  private BigDecimal balance;
  private LocalDate date;
}
