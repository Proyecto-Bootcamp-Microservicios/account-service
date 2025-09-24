package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.BalanceCollection;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
@TypeAlias("VIP_SAVING")
@Getter
@Setter
public class VipSavingAccountCollection extends SavingAccountCollection {
  private BigDecimal minimumDailyAverage;

  public VipSavingAccountCollection(String id, String customerId, String customerType, String accountType, String accountNumber, String externalAccountNumber, String status, BalanceCollection balance, List<AccountHolderCollection> holders, LocalDateTime createdAt, LocalDateTime updatedAt, TransactionLimitCollection transactionLimit, BigDecimal minimumDailyAverage) {
    super(id, customerId, customerType, accountType, accountNumber, externalAccountNumber, status, balance, holders, createdAt, updatedAt, transactionLimit);
    this.minimumDailyAverage = minimumDailyAverage;
  }
}