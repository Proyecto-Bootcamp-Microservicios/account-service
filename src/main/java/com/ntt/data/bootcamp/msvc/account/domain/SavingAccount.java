package com.ntt.data.bootcamp.msvc.account.domain;

import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.enums.OperationType;
import com.ntt.data.bootcamp.msvc.account.domain.util.AccountNumberGenerator;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.ntt.data.bootcamp.msvc.account.domain.vo.AccountHolder;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Audit;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Balance;
import com.ntt.data.bootcamp.msvc.account.domain.vo.TransactionLimit;
import lombok.Getter;

@Getter
public class SavingAccount extends Account {

  private SavingAccount(
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
      TransactionLimit transactionLimit) {
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
    validateBusinessRules();
  }

  public static SavingAccount of(
      String customerId, String customerType, String documentType, String documentNumber) {
    String internalAccountNumber = AccountNumberGenerator.generateInternal();
    String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);
    return new SavingAccount(
        UUID.randomUUID().toString(),
        customerId,
        customerType,
        documentType,
        documentNumber,
        internalAccountNumber,
        externalAccountNumber,
        AccountType.SAVING,
        AccountStatus.ACTIVE,
        Balance.zero("PEN"),
        Audit.create(),
        new HashSet<>(),
        TransactionLimit.of());
  }

  public static SavingAccount of(
      String customerId,
      String customerType,
      String documentType,
      String documentNumber,
      BigDecimal amount) {
    String internalAccountNumber = AccountNumberGenerator.generateInternal();
    String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);
    return new SavingAccount(
        UUID.randomUUID().toString(),
        customerId,
        customerType,
        documentType,
        documentNumber,
        internalAccountNumber,
        externalAccountNumber,
        AccountType.SAVING,
        AccountStatus.ACTIVE,
        Balance.of("PEN", amount),
        Audit.create(),
        new HashSet<>(),
        TransactionLimit.of());
  }

  public static SavingAccount reconstruct(
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
      TransactionLimit transactionLimit) {
    return new SavingAccount(
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
  }

  @Override
  protected void validateBusinessRules() {
    if (isEnterpriseAccount())
      throw new IllegalArgumentException("Enterprise accounts are not allowed");
    if (this.balance == null) throw new IllegalArgumentException("Balance cannot be null");
    if (this.holders == null || this.holders.isEmpty())
      throw new IllegalArgumentException("Account must have at least one holder");
    if (this.transactionLimit == null)
      throw new IllegalArgumentException("Transaction limit cannot be null");
  }

  @Override
  protected void canPerformTransactionSpecific(OperationType operation, BigDecimal amount) {
    // This method is not used for checking accounts
  }

  @Override
  public SavingAccount addHolder(AccountHolder holder) {
    throw new UnsupportedOperationException("Savings accounts cannot have multiple holders");
  }

  @Override
  public SavingAccount removeHolder(AccountHolder accountHolder) {
    throw new UnsupportedOperationException("Savings accounts cannot have multiple holders");
  }

  @Override
  public Account changeStatus(AccountStatus status) {
    if (this.getStatus().equals(status))
      throw new IllegalArgumentException("Account status cannot be the same");
    return new SavingAccount(
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
        this.transactionLimit);
  }

  @Override
  public Account withNewBalance(Balance newBalance) {
    return new SavingAccount(
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
        this.transactionLimit);
  }

  @Override
  public Account withNewTransactionLimit(TransactionLimit newTransactionLimit) {
    return new SavingAccount(
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
        newTransactionLimit);
  }

  @Override
  public Account updateOperationDateIfNeeded() {
    return this;
  }
}
