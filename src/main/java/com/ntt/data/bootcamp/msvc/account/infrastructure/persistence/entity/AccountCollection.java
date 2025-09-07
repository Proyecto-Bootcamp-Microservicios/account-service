package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.BalanceCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
@Getter
@Setter
@AllArgsConstructor
public abstract class AccountCollection {

  @Id private String id;
  private String customerId;
  private String customerType;
  private String accountType;
  @Indexed private String accountNumber;
  @Indexed private String externalAccountNumber;
  private String status;

  private BalanceCollection balance;
  private List<AccountHolderCollection> holders;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private TransactionLimitCollection transactionLimit;
}
