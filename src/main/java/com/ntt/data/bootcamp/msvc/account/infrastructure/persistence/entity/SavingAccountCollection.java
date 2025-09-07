package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.BalanceCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
@Getter
@Setter
@TypeAlias("SAVING")
public class SavingAccountCollection extends AccountCollection {

  public SavingAccountCollection(
      String id,
      String customerId,
      String customerType,
      String accountType,
      String accountNumber,
      String externalAccountNumber,
      String status,
      BalanceCollection balance,
      List<AccountHolderCollection> holders,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
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
  }
}
