package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "daily_balances")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DailyBalanceSnapshotCollection {
  @Id
  private String id;
  private String accountId;
  private String customerId;
  private String accountType;
  private BigDecimal balance;
  private String currency;
  private LocalDate date;
  private LocalDateTime capturedAt;
}
