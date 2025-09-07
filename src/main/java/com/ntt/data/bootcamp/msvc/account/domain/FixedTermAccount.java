package com.ntt.data.bootcamp.msvc.account.domain;

import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.enums.OperationType;
import com.ntt.data.bootcamp.msvc.account.domain.util.AccountNumberGenerator;
import com.ntt.data.bootcamp.msvc.account.domain.vo.AccountHolder;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Audit;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Balance;
import com.ntt.data.bootcamp.msvc.account.domain.vo.TransactionLimit;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import lombok.Getter;

@Getter
public class FixedTermAccount extends Account {
  private final LocalDate maturityDate;
  private final int dayOfOperation;
  private final BigDecimal interestRate;
  private final boolean hasPerformedMonthlyOperation;

  private FixedTermAccount(
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
      LocalDate maturityDate,
      int dayOfOperation,
      BigDecimal interestRate,
      boolean hasPerformedMonthlyOperation,
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

    this.maturityDate = maturityDate;
    this.dayOfOperation = dayOfOperation;
    this.interestRate = interestRate;
    this.hasPerformedMonthlyOperation = hasPerformedMonthlyOperation;
    validateBusinessRules();
  }

  public static FixedTermAccount ofSemiAnnually(
      String customerId,
      String customerType,
      String documentType,
      String documentNumber,
      BigDecimal initialBalance) {

    String internalAccountNumber = AccountNumberGenerator.generateInternal();
    String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);
    LocalDate maturityDate = LocalDate.now().plusYears(1).withDayOfMonth(1);

    return new FixedTermAccount(
        UUID.randomUUID().toString(),
        customerId,
        customerType,
        documentType,
        documentNumber,
        internalAccountNumber,
        externalAccountNumber,
        AccountType.FIXED_TERM,
        AccountStatus.ACTIVE,
        Balance.of("PEN", initialBalance),
        Audit.create(),
        new HashSet<>(),
        maturityDate,
        15,
        BigDecimal.valueOf(0.02),
        false,
        TransactionLimit.ofFixedTermAccount());
  }

  public static FixedTermAccount ofAnnually(
      String customerId,
      String customerType,
      String documentType,
      String documentNumber,
      BigDecimal initialBalance) {

    String internalAccountNumber = AccountNumberGenerator.generateInternal();
    String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);
    LocalDate maturityDate = LocalDate.now().plusYears(1).withDayOfMonth(1);

    return new FixedTermAccount(
        UUID.randomUUID().toString(),
        customerId,
        customerType,
        documentType,
        documentNumber,
        internalAccountNumber,
        externalAccountNumber,
        AccountType.FIXED_TERM,
        AccountStatus.ACTIVE,
        Balance.of("PEN", initialBalance),
        Audit.create(),
        new HashSet<>(),
        maturityDate,
        15,
        BigDecimal.valueOf(0.05),
        false,
        TransactionLimit.ofFixedTermAccount());
  }

  public static FixedTermAccount reconstruct(
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
      LocalDate maturityDate,
      int dayOfOperation,
      BigDecimal interestRate,
      boolean hasPerformedMonthlyOperation,
      TransactionLimit transactionLimit) {
    return new FixedTermAccount(
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
        maturityDate,
        dayOfOperation,
        interestRate,
        hasPerformedMonthlyOperation,
        transactionLimit);
  }

  @Override
  protected void validateBusinessRules() {
    if (isEnterpriseAccount())
      throw new IllegalArgumentException("Enterprise accounts are not allowed");
    if (this.balance.getAmount().compareTo(BigDecimal.valueOf(1000)) < 0)
      throw new IllegalArgumentException(
          "Fixed term account requires minimum initial balance of 1000");
    if (interestRate.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("Interest rate must be positive");
  }

  @Override
  protected void canPerformTransactionSpecific(OperationType operation, BigDecimal amount) {
    if (LocalDate.now().getDayOfMonth() != this.dayOfOperation)
      throw new IllegalArgumentException(
          "Fixed term accounts cannot perform operations on operation date");
    if (hasPerformedMonthlyOperation)
      throw new IllegalArgumentException(
          "Fixed term accounts can only perform one operation per month");
  }

  @Override
  public TransactionLimit incrementCurrentTransaction(OperationType operationType) {
    TransactionLimit transactionLimit = getTransactionLimit();
    Map<OperationType, Integer> newCounts =
        new HashMap<>(transactionLimit.getCurrentTransactions());
    for (OperationType type : newCounts.keySet()) {
      int count = newCounts.getOrDefault(type, 0);
      newCounts.put(type, count + 1);
    }
    return TransactionLimit.reconstruct(
        transactionLimit.getMaxFreeTransactions(),
        transactionLimit.getFixedCommissions(),
        transactionLimit.getPercentageCommissions(),
        newCounts,
        transactionLimit.getMonthStartDate());
  }

  public boolean isOperationDate() {
    LocalDate today = LocalDate.now();
    return today.getDayOfMonth() == this.dayOfOperation;
  }

  public boolean canPerformMonthlyOperation() {
    return this.isOperationDate() && !this.hasPerformedMonthlyOperation && this.isActive();
  }

  public boolean isMatured() {
    return LocalDate.now().isAfter(this.maturityDate);
  }

  @Override
  public FixedTermAccount addHolder(AccountHolder holder) {
    throw new UnsupportedOperationException("Fixed term accounts cannot have multiple holders");
  }

  @Override
  public FixedTermAccount removeHolder(AccountHolder accountHolder) {
    throw new UnsupportedOperationException("Fixed term accounts cannot have multiple holders");
  }

  @Override
  public Account changeStatus(AccountStatus status) {
    if (this.getStatus().equals(status))
      throw new IllegalArgumentException("Account status cannot be the same");
    return new FixedTermAccount(
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
        this.maturityDate,
        this.dayOfOperation,
        this.interestRate,
        this.hasPerformedMonthlyOperation,
        this.transactionLimit);
  }

  @Override
  public Account withNewBalance(Balance newBalance) {
    return new FixedTermAccount(
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
        this.maturityDate,
        this.dayOfOperation,
        this.interestRate,
        true,
        this.transactionLimit);
  }

  @Override
  public Account withNewTransactionLimit(TransactionLimit newTransactionLimit) {
    return new FixedTermAccount(
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
        this.maturityDate,
        this.dayOfOperation,
        this.interestRate,
        true,
        newTransactionLimit);
  }

  public Account updateOperationDateIfNeeded() {
    LocalDate currentDate = LocalDate.now();
    int currentMonthOperationDate = calculateOperationDateForMonth(currentDate).getDayOfMonth();

    if (this.dayOfOperation < currentMonthOperationDate) {
      return new FixedTermAccount(
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
          this.maturityDate,
          currentMonthOperationDate,
          this.interestRate,
          false,
          this.transactionLimit);
    }
    return this;
  }

  private LocalDate calculateOperationDateForMonth(LocalDate date) {
    int dayOfOperation = 15;
    int lastDayOfMonth = date.lengthOfMonth();
    int actualDay = Math.min(dayOfOperation, lastDayOfMonth);
    return date.withDayOfMonth(actualDay);
  }
}
