package com.ntt.data.bootcamp.msvc.account.domain;

import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.enums.OperationType;
import com.ntt.data.bootcamp.msvc.account.domain.vo.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public abstract class Account {
  protected final AccountId id;
  protected final String customerId;
  protected final String customerType;
  protected final AccountType accountType;
  protected final InternalAccountNumber accountNumber;
  protected final ExternalAccountNumber externalAccountNumber;
  protected final AccountStatus status;
  protected final Balance balance;
  protected final Audit audit;
  protected final Set<AccountHolder> holders;
  protected final TransactionLimit transactionLimit;

  protected Account(
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
    this.id = AccountId.of(id);
    this.customerId = customerId;
    this.customerType = customerType;
    this.accountNumber = InternalAccountNumber.of(accountNumber);
    this.externalAccountNumber = ExternalAccountNumber.of(externalAccountNumber);
    this.accountType = accountType;
    this.status = status;
    this.balance = balance;
    this.audit = audit;
    this.holders = new HashSet<>(holders);
    this.transactionLimit = transactionLimit;
    if (this.holders.isEmpty()) {
      this.holders.add(AccountHolder.ofPrimaryHolder(documentType, documentNumber));
    }
  }

  protected abstract void canPerformTransactionSpecific(OperationType operation, BigDecimal amount);

  protected abstract void validateBusinessRules();

  public abstract Account addHolder(AccountHolder newHolder);

  public abstract Account removeHolder(AccountHolder accountHolder);

  public abstract Account changeStatus(AccountStatus status);

  public abstract Account withNewBalance(Balance newBalance);

  public abstract Account withNewTransactionLimit(TransactionLimit newTransactionLimit);

  public abstract Account updateOperationDateIfNeeded();

  /*METODOS GET DE VO*/
  // Id VO
  public String getIdValue() {
    return this.id.getValue();
  }

  public String getDocumentType() {
    return this.getPrimaryHolder().getDocumentType();
  }

  public String getDocumentNumber() {
    return this.getPrimaryHolder().getDocumentNumber();
  }

  // Balance VO
  public BigDecimal getAmount() {
    return this.balance.getAmount();
  }

  public Currency getCurrency() {
    return this.balance.getCurrency();
  }

  public String getCurrencyCode() {
    return getCurrency().getCurrencyCode();
  }

  public String getDisplayAmount() {
    return getCurrency().getSymbol() + " " + getAmount().toString();
  }

  public boolean hasSameCurrency(Account other) {
    return this.getCurrencyCode().equals(other.getCurrencyCode());
  }

  public boolean isZero() {
    return getAmount().compareTo(BigDecimal.ZERO) == 0;
  }

  public boolean isPositive() {
    return getAmount().compareTo(BigDecimal.ZERO) > 0;
  }

  // AccountNumber VO
  public String getAccountNumber() {
    return this.accountNumber.getValue();
  }

  public String getExternalAccountNumber() {
    return this.externalAccountNumber.getValue();
  }

  // Audit VO
  public LocalDateTime getCreatedAt() {
    return this.audit.getCreatedAt();
  }

  public LocalDateTime getUpdatedAt() {
    return this.audit.getUpdatedAt();
  }

  /*METODOS DE VALIDACIÓN*/
  public boolean isPersonalAccount() {
    return "PERSONAL".equals(this.customerType);
  }

  public boolean isEnterpriseAccount() {
    return "ENTERPRISE".equals(this.customerType);
  }

  public boolean isActive() {
    return this.status == AccountStatus.ACTIVE;
  }

  public boolean isBlocked() {
    return this.status == AccountStatus.BLOCKED;
  }

  public boolean isClosed() {
    return this.status == AccountStatus.CLOSED;
  }

  public boolean isSuspended() {
    return this.status == AccountStatus.SUSPENDED;
  }

  public AccountHolder getPrimaryHolder() {
    return this.holders.stream()
        .filter(AccountHolder::isPrimaryHolder)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Primary holder not found"));
  }

  public void validateTransaction(OperationType operation, BigDecimal amount) {
    if (!this.isActive())
      throw new IllegalArgumentException(
          "Account is " + this.status.name().toLowerCase() + " and cannot perform transactions");
    if ((operation == OperationType.WITHDRAWAL || operation == OperationType.TRANSFER_INTERNAL || operation == OperationType.TRANSFER_EXTERNAL|| operation == OperationType.PAYMENT)
        && !this.hasSufficientFunds(amount))
      throw new IllegalArgumentException("Insufficient funds");
    this.canPerformTransactionSpecific(operation, amount);
  }

  public final Balance calculateNewBalance(OperationType operation, BigDecimal amount) {
    if (operation == OperationType.DEPOSIT) {
      return this.balance.update(amount);
    } else if (operation == OperationType.WITHDRAWAL || operation == OperationType.TRANSFER_INTERNAL || operation == OperationType.TRANSFER_EXTERNAL || operation == OperationType.PAYMENT) {
      return this.balance.update(amount.negate());
    }

    throw new IllegalArgumentException("Operation not supported: " + operation);
  }

  public boolean hasSufficientFunds(BigDecimal amount) {
    return this.getAmount().compareTo(amount) >= 0;
  }

  public BigDecimal calculateAmountWithCommission(OperationType operationType, BigDecimal amount) {
    if (this.isFreeTransaction(operationType)) return amount;

    BigDecimal commission = this.getCommissionPerType(operationType, amount);

    if(operationType == OperationType.WITHDRAWAL
        || operationType == OperationType.TRANSFER_INTERNAL
        || operationType == OperationType.TRANSFER_EXTERNAL
        || operationType == OperationType.PAYMENT ) return amount.subtract(commission);
    return amount.add(commission);
  }

  public BigDecimal getCommissionPerType(OperationType operationType, BigDecimal amount) {
    TransactionLimit transactionLimit = getTransactionLimit();
    return isFreeTransaction(operationType) || isUnlimited(operationType)
        ? BigDecimal.ZERO
        : (transactionLimit
                .getFixedCommissionPerType(operationType)
                .add(transactionLimit.getPercentageCommissionPerType(operationType))
                .multiply(amount))
            .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
            .min(TransactionLimit.MAX_COMMISSION)
            .max(TransactionLimit.MIN_COMMISSION);
  }

  public boolean isFreeTransaction(OperationType operationType) {
    return this.remainingFreeMovements(operationType) > 0;
  }

  public int remainingFreeMovements(OperationType operationType) {
    TransactionLimit transactionLimit = getTransactionLimit();
    int maxFree = transactionLimit.getFreeTransactionsPerType(operationType);
    int currentCount = transactionLimit.getCurrentTransactionsPerType(operationType);
    return Math.max(0, maxFree - currentCount);
  }

  public boolean isUnlimited(OperationType operationType) {
    TransactionLimit transactionLimit = getTransactionLimit();
    return transactionLimit.getMaxFreeTransactions().getOrDefault(operationType, 0)
        == TransactionLimit.UNLIMITED;
  }

  public TransactionLimit incrementCurrentTransaction(OperationType operationType) {
    TransactionLimit transactionLimit = getTransactionLimit();
    Map<OperationType, Integer> newCounts =
        new HashMap<>(transactionLimit.getCurrentTransactions());
    int currentCount = newCounts.getOrDefault(operationType, 0);
    newCounts.put(operationType, currentCount + 1);

    return TransactionLimit.reconstruct(
        transactionLimit.getMaxFreeTransactions(),
        transactionLimit.getFixedCommissions(),
        transactionLimit.getPercentageCommissions(),
        newCounts,
        transactionLimit.getMonthStartDate());
  }

  /*METODOS DE MODIFICACIÓN (FALTA REFACTORIZAR PARA RETORNAR UN NUEVO OBJETO)*/
  /** @param newPercentages key documentNumber, value percentage */
  public void updateAllPercentages(Map<String, BigDecimal> newPercentages) {
    validateTotalEquals100(newPercentages);
    validateAllHoldersExist(newPercentages);
    updateAllHoldersPercentages(newPercentages);
  }

  private void validateTotalEquals100(Map<String, BigDecimal> newPercentages) {
    BigDecimal total = newPercentages.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

    if (total.compareTo(BigDecimal.valueOf(100)) != 0)
      throw new IllegalArgumentException("Total percentage must be 100%");
  }

  private void validateAllHoldersExist(Map<String, BigDecimal> newPercentages) {
    Set<String> documentNumbersFromMap = newPercentages.keySet();

    Set<String> documentNumbersFromAccount =
        this.holders.stream().map(AccountHolder::getDocumentNumber).collect(Collectors.toSet());

    if (!documentNumbersFromAccount.equals(documentNumbersFromMap))
      throw new IllegalArgumentException("All holders must exist in the account");
  }

  private void updateAllHoldersPercentages(Map<String, BigDecimal> newPercentages) {
    Set<AccountHolder> updatedHolders = new HashSet<>();

    for (AccountHolder existingHolder : this.holders) {
      String documentNumber = existingHolder.getDocumentNumber();
      BigDecimal newPercentage = newPercentages.get(documentNumber);

      if (existingHolder.isPrimaryHolder()) {
        AccountHolder updatedPrimaryHolder =
            AccountHolder.ofPrimaryHolder(
                existingHolder.getDocumentType(), documentNumber, newPercentage);
        updatedHolders.add(updatedPrimaryHolder);
      } else {
        AccountHolder updatedHolder =
            AccountHolder.ofSecondaryHolder(
                existingHolder.getDocumentType(), documentNumber, newPercentage);
        updatedHolders.add(updatedHolder);
      }
    }
    this.holders.clear();
    this.holders.addAll(updatedHolders);
  }
}
