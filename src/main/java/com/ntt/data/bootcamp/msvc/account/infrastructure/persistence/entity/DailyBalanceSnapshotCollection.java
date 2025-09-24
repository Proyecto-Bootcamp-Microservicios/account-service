package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "account_daily_balances")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DailyBalanceSnapshotCollection {

  /*to json example: {"id": "1",
  "accountId": "8edfca62-dc01-4c0c-aca1-643858257f0d",
  "customerId": "0dcad533-7e50-4690-96ec-e21fe4406126",
  "accountType": "CHECKING",
  "balance": 100,
  "currency": "PEN",
  "date": "2025-09-18",
  "capturedAt": "2025-09-18T00:00:00"}*/
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
