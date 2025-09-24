package com.ntt.data.bootcamp.msvc.account.domain;

import com.ntt.data.bootcamp.msvc.account.domain.enums.*;
import com.ntt.data.bootcamp.msvc.account.domain.vo.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * Base domain aggregate representing a bank account.
 * Contains shared business rules and behaviors for all account types.
 */
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

  /**
   * Constructs a new account aggregate from primitive values and value objects.
   */
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

  /** Hook for account-type-specific transaction validation. */
  protected abstract void canPerformTransactionSpecific(OperationDirection direction, BigDecimal amount);

  /** Validates invariants and business rules for the account. */
  protected abstract void validateBusinessRules();

  /** Returns a new account instance with the holder added. */
  public abstract Account addHolder(AccountHolder newHolder);

  /** Returns a new account instance with the holder removed. */
  public abstract Account removeHolder(AccountHolder accountHolder);

  /** Returns a new account instance with the updated status. */
  public abstract Account changeStatus(AccountStatus status);

  /** Returns a new account instance with the updated balance. */
  public abstract Account withNewBalance(Balance newBalance);

  /** Returns a new account instance with the updated transaction limit. */
  public abstract Account withNewTransactionLimit(TransactionLimit newTransactionLimit);

  /** Returns a new account instance updating operation date if a new period started. */
  public abstract Account updateOperationDateIfNeeded();

  /*METODOS GET DE VO*/
  // Id VO
  /** Gets the string value of the id value object. */
  public String getIdValue() {
    return this.id.getValue();
  }

  /** Gets the document type of the primary holder. */
  public String getDocumentType() {
    return this.getPrimaryHolder().getDocumentType();
  }

  /** Gets the document number of the primary holder. */
  public String getDocumentNumber() {
    return this.getPrimaryHolder().getDocumentNumber();
  }

  // Balance VO
  /** Gets the current monetary amount. */
  public BigDecimal getAmount() {
    return this.balance.getAmount();
  }

  /** Gets the current currency. */
  public Currency getCurrency() {
    return this.balance.getCurrency();
  }

  /** Gets the ISO currency code of the balance. */
  public String getCurrencyCode() {
    return getCurrency().getCurrencyCode();
  }

  /** Gets a human-readable representation of the balance with symbol. */
  public String getDisplayAmount() {
    return getCurrency().getSymbol() + " " + getAmount().toString();
  }

  /** Checks if two accounts share the same currency. */
  public boolean hasSameCurrency(Account other) {
    return this.getCurrencyCode().equals(other.getCurrencyCode());
  }

  /** True when the amount is exactly zero. */
  public boolean isZero() {
    return getAmount().compareTo(BigDecimal.ZERO) == 0;
  }

  /** True when the amount is strictly positive. */
  public boolean isPositive() {
    return getAmount().compareTo(BigDecimal.ZERO) > 0;
  }

  // AccountNumber VO
  /** Gets the internal account number value. */
  public String getAccountNumber() {
    return this.accountNumber.getValue();
  }

  /** Gets the external account number value. */
  public String getExternalAccountNumber() {
    return this.externalAccountNumber.getValue();
  }

  // Audit VO
  /** Gets creation timestamp. */
  public LocalDateTime getCreatedAt() {
    return this.audit.getCreatedAt();
  }

  /** Gets last update timestamp. */
  public LocalDateTime getUpdatedAt() {
    return this.audit.getUpdatedAt();
  }

  /*METODOS DE VALIDACIÓN*/
  /** True when the account belongs to a personal customer. */
  public boolean isPersonalAccount() {
    return "PERSONAL".equals(this.customerType);
  }

  /** True when the account belongs to an enterprise customer. */
  public boolean isEnterpriseAccount() {
    return "ENTERPRISE".equals(this.customerType);
  }

  /** True when the account status is ACTIVE. */
  public boolean isActive() {
    return this.status == AccountStatus.ACTIVE;
  }

  /** True when the account status is BLOCKED. */
  public boolean isBlocked() {
    return this.status == AccountStatus.BLOCKED;
  }

  /** True when the account status is CLOSED. */
  public boolean isClosed() {
    return this.status == AccountStatus.CLOSED;
  }

  /** True when the account status is SUSPENDED. */
  public boolean isSuspended() {
    return this.status == AccountStatus.SUSPENDED;
  }

  /** Returns the primary holder. */
  public AccountHolder getPrimaryHolder() {
    return this.holders.stream()
        .filter(AccountHolder::isPrimaryHolder)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Primary holder not found"));
  }

  /** Validates business rules for a transaction before applying it. */
  public void validateTransaction(OperationDirection direction, BigDecimal amount) {
    if (!this.isActive())
      throw new IllegalArgumentException(
          "Account is " + this.status.name().toLowerCase() + " and cannot perform transactions");
    if (direction == OperationDirection.OUT
        && !this.hasSufficientFunds(amount))
      throw new IllegalArgumentException("Insufficient funds");
    this.canPerformTransactionSpecific(direction, amount);
  }

  /** Calculates the new balance after applying the transaction amount and direction. */
  public final Balance calculateNewBalance(OperationDirection direction, BigDecimal amount) {
    if (direction == OperationDirection.IN) {
      return this.balance.update(amount);
    } else if (direction == OperationDirection.OUT) {
      return this.balance.update(amount.negate());
    }

    throw new IllegalArgumentException("Operation not supported: " + direction);
  }

  /** Checks if the account has enough funds for a debit of the given amount. */
  public boolean hasSufficientFunds(BigDecimal amount) {
    return this.getAmount().compareTo(amount) >= 0;
  }

  /** Calculates the net amount including commission based on the operation type. */
  public BigDecimal calculateAmountWithCommission(OperationType operationType, BigDecimal amount) {
    if (this.isFreeTransaction(operationType)) return amount;

    BigDecimal commission = this.getCommissionPerType(operationType, amount);

    if (operationType == OperationType.WITHDRAWAL
        || operationType == OperationType.TRANSFER_INTERNAL
        || operationType == OperationType.TRANSFER_EXTERNAL
        || operationType == OperationType.PAYMENT) {
      return amount.add(commission);
    }

    BigDecimal net = amount.subtract(commission);
    if (net.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("The commission cannot be greater than or equal to the amount");
    return net;
  }

  /** Computes the commission amount for the given operation and amount. */
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

  /** True when a free transaction is still available for the given type. */
  public boolean isFreeTransaction(OperationType operationType) {
    return this.remainingFreeMovements(operationType) > 0;
  }

  /** Remaining number of free transactions for the given type in the current period. */
  public int remainingFreeMovements(OperationType operationType) {
    TransactionLimit transactionLimit = getTransactionLimit();
    int maxFree = transactionLimit.getFreeTransactionsPerType(operationType);
    int currentCount = transactionLimit.getCurrentTransactionsPerType(operationType);
    return Math.max(0, maxFree - currentCount);
  }

  /** True when the given operation type is unlimited. */
  public boolean isUnlimited(OperationType operationType) {
    TransactionLimit transactionLimit = getTransactionLimit();
    return transactionLimit.getMaxFreeTransactions().getOrDefault(operationType, 0)
        == TransactionLimit.UNLIMITED;
  }

  /** Increments the current transaction counter for the given type and returns a new limit object. */
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
  /** Updates ownership percentages for all holders, validating they sum to 100%. */
  public void updateAllPercentages(Map<String, BigDecimal> newPercentages) {
    validateTotalEquals100(newPercentages);
    validateAllHoldersExist(newPercentages);
    updateAllHoldersPercentages(newPercentages);
  }

  /** Validates the sum of percentages equals 100. */
  private void validateTotalEquals100(Map<String, BigDecimal> newPercentages) {
    BigDecimal total = newPercentages.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

    if (total.compareTo(BigDecimal.valueOf(100)) != 0)
      throw new IllegalArgumentException("Total percentage must be 100%");
  }

  /** Validates that all holders exist in the provided map. */
  private void validateAllHoldersExist(Map<String, BigDecimal> newPercentages) {
    Set<String> documentNumbersFromMap = newPercentages.keySet();

    Set<String> documentNumbersFromAccount =
        this.holders.stream().map(AccountHolder::getDocumentNumber).collect(Collectors.toSet());

    if (!documentNumbersFromAccount.equals(documentNumbersFromMap))
      throw new IllegalArgumentException("All holders must exist in the account");
  }

  /** Applies the percentages to holders preserving primary designation. */
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
