package com.ntt.data.bootcamp.msvc.account.domain;

import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.enums.OperationDirection;
import com.ntt.data.bootcamp.msvc.account.domain.util.AccountNumberGenerator;
import com.ntt.data.bootcamp.msvc.account.domain.vo.AccountHolder;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Audit;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Balance;
import com.ntt.data.bootcamp.msvc.account.domain.vo.TransactionLimit;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class VipSavingAccount extends SavingAccount {
  private final BigDecimal minimumDailyAverage;

  private VipSavingAccount(
      String id,
      String customerId,
      String customerType,
      String documentType,
      String documentNumber,
      String accountNumber,
      String externalAccountNumber,
      AccountType accountType,
      AccountStatus status,
      Balance balance,
      Audit audit,
      Set<AccountHolder> holders,
      TransactionLimit transactionLimit,
      BigDecimal minimumDailyAverage) {
    super(
        id,
        customerId,
        customerType,
        documentType,
        documentNumber,
        accountNumber,
        externalAccountNumber,
        accountType,
        status,
        balance,
        audit,
        holders,
        transactionLimit);
    this.minimumDailyAverage = minimumDailyAverage;
    validateBusinessRules();
  }

  public static VipSavingAccount of(
      String customerId, String customerType, String documentType, String documentNumber) {
    String internalAccountNumber = AccountNumberGenerator.generateInternal();
    String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);
    BigDecimal minimumDailyAverage = BigDecimal.valueOf(5000); // 5000 PEN mínimo

    return new VipSavingAccount(
        UUID.randomUUID().toString(),
        customerId,
        customerType,
        documentType,
        documentNumber,
        internalAccountNumber,
        externalAccountNumber,
        AccountType.VIP_SAVING,
        AccountStatus.ACTIVE,
        Balance.zero("PEN"),
        Audit.create(),
        new HashSet<>(),
        TransactionLimit.of(),
        minimumDailyAverage);
  }

  public static VipSavingAccount reconstruct(
      String id,
      String customerId,
      String customerType,
      String documentType,
      String documentNumber,
      String accountNumber,
      String externalAccountNumber,
      AccountType accountType,
      AccountStatus status,
      Balance balance,
      Audit audit,
      Set<AccountHolder> holders,
      TransactionLimit transactionLimit,
      BigDecimal minimumDailyAverage) {
    return new VipSavingAccount(
        id,
        customerId,
        customerType,
        documentType,
        documentNumber,
        accountNumber,
        externalAccountNumber,
        accountType,
        status,
        balance,
        audit,
        holders,
        transactionLimit,
        minimumDailyAverage);
  }

  @Override
  protected void validateBusinessRules() {
    super.validateBusinessRules(); // Llamar validaciones de SavingAccount

    if (this.minimumDailyAverage == null || this.minimumDailyAverage.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Minimum daily average must be positive");
    }
  }

  @Override
  protected void canPerformTransactionSpecific(OperationDirection direction, BigDecimal amount) {
    // Sin validaciones específicas, se valida mensualmente
  }

  @Override
  public VipSavingAccount addHolder(AccountHolder holder) {
    throw new UnsupportedOperationException("VIP savings accounts cannot have multiple holders");
  }

  @Override
  public VipSavingAccount removeHolder(AccountHolder accountHolder) {
    throw new UnsupportedOperationException("VIP savings accounts cannot have multiple holders");
  }

  @Override
  public Account changeStatus(AccountStatus status) {
    if (this.getStatus().equals(status)) {
      throw new IllegalArgumentException("Account status cannot be the same");
    }
    return new VipSavingAccount(
        this.getIdValue(),
        this.customerId,
        this.customerType,
        this.getDocumentType(),
        this.getDocumentNumber(),
        this.getAccountNumber(),
        this.getExternalAccountNumber(),
        this.accountType,
        status,
        this.balance,
        this.audit.update(),
        this.holders,
        this.transactionLimit,
        this.minimumDailyAverage);
  }

  @Override
  public Account withNewBalance(Balance newBalance) {
    return new VipSavingAccount(
        this.getIdValue(),
        this.customerId,
        this.customerType,
        this.getDocumentType(),
        this.getDocumentNumber(),
        this.getAccountNumber(),
        this.getExternalAccountNumber(),
        this.accountType,
        this.status,
        newBalance,
        this.audit.update(),
        this.holders,
        this.transactionLimit,
        this.minimumDailyAverage);
  }

  @Override
  public Account withNewTransactionLimit(TransactionLimit newTransactionLimit) {
    return new VipSavingAccount(
        this.getIdValue(),
        this.customerId,
        this.customerType,
        this.getDocumentType(),
        this.getDocumentNumber(),
        this.getAccountNumber(),
        this.getExternalAccountNumber(),
        this.accountType,
        this.status,
        this.balance,
        this.audit.update(),
        this.holders,
        newTransactionLimit,
        this.minimumDailyAverage);
  }

  @Override
  public Account updateOperationDateIfNeeded() {
    return this;
  }
}