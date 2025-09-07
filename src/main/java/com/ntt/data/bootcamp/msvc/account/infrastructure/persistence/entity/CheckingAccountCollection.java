package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity;

import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded.AuthorizedSignerCollection;
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
@TypeAlias(value = "CHECKING")
@Getter
@Setter
public class CheckingAccountCollection extends AccountCollection {
  private BigDecimal maintenanceFee;
  private LocalDate nextFeeDate;
  private List<AuthorizedSignerCollection> authorizedSigners;

  public CheckingAccountCollection(
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
      BigDecimal maintenanceFee,
      LocalDate nextFeeDate,
      List<AuthorizedSignerCollection> authorizedSigners,
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
    this.maintenanceFee = maintenanceFee;
    this.nextFeeDate = nextFeeDate;
    this.authorizedSigners = authorizedSigners;
  }
}
