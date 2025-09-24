package com.ntt.data.bootcamp.msvc.account.application.dto.response;

import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerBalanceReportResponse {
  private String customerId;
  private LocalDate month;
  private Map<AccountType, BigDecimal> averageBalancesByProduct;
  private BigDecimal totalAverageBalance;
  private String currency;
  private int totalAccounts;
}