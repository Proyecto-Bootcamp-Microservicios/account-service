package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AuthorizedSignerCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.BalanceCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "accounts")
@TypeAlias("PYME_CHECKING")
@Getter
@Setter
public class PymeCheckingAccountCollection extends CheckingAccountCollection {
  public PymeCheckingAccountCollection(String id, String customerId, String customerType, String accountType, String accountNumber, String externalAccountNumber, String status, List<AccountHolderCollection> holders, BalanceCollection balance, LocalDateTime createdAt, LocalDateTime updatedAt, BigDecimal maintenanceFee, LocalDate nextFeeDate, List<AuthorizedSignerCollection> authorizedSigners, TransactionLimitCollection transactionLimit) {
    super(id, customerId, customerType, accountType, accountNumber, externalAccountNumber, status, holders, balance, createdAt, updatedAt, maintenanceFee, nextFeeDate, authorizedSigners, transactionLimit);
  }
}