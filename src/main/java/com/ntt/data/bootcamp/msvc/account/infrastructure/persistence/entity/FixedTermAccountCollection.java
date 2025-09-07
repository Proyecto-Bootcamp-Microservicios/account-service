package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.BalanceCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
@Getter
@Setter
@TypeAlias("FIXED_TERM")
public class FixedTermAccountCollection extends AccountCollection {

  private LocalDate maturityDate;
  private int dayOfOperation;
  private BigDecimal interestRate;
  private boolean hasPerformedMonthlyOperation;

  public FixedTermAccountCollection(
      String id,
      String customerId,
      String customerType,
      String accountType,
      String accountNumber,
      String externalAccountNumber,
      String status,
      List<AccountHolderCollection> holders,
      BalanceCollection balance,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDate maturityDate,
      int dayOfOperation,
      BigDecimal interestRate,
      boolean hasPerformedMonthlyOperation,
      TransactionLimitCollection transactionLimit) {
    super(
        id,
        customerId,
        customerType,
        accountType,
        accountNumber,
        externalAccountNumber,
        status,
        balance,
        holders,
        createdAt,
        updatedAt,
        transactionLimit);
    this.maturityDate = maturityDate;
    this.dayOfOperation = dayOfOperation;
    this.interestRate = interestRate;
    this.hasPerformedMonthlyOperation = hasPerformedMonthlyOperation;
  }
}
