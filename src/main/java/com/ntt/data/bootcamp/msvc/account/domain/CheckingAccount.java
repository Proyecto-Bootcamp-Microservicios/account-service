package com.ntt.data.bootcamp.msvc.account.domain;

import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.enums.OperationDirection;
import com.ntt.data.bootcamp.msvc.account.domain.enums.OperationType;
import com.ntt.data.bootcamp.msvc.account.domain.util.AccountNumberGenerator;
import com.ntt.data.bootcamp.msvc.account.domain.vo.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.ntt.data.bootcamp.msvc.account.domain.vo.*;
import lombok.Getter;

@Getter
public class CheckingAccount extends Account {
  protected final BigDecimal maintenanceFee;
  protected final LocalDate nextFeeDate;
  protected final Set<AuthorizedSigner> signers;

  protected CheckingAccount(
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
      BigDecimal maintenanceFee,
      LocalDate nextFeeDate,
      Set<AuthorizedSigner> signers,
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
    this.maintenanceFee = maintenanceFee;
    this.nextFeeDate = nextFeeDate;
    this.signers = signers;
    validateBusinessRules();
  }

  public static CheckingAccount of(
      String customerId, String customerType, String documentType, String documentNumber) {

    String internalAccountNumber = AccountNumberGenerator.generateInternal();
    String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);

    return new CheckingAccount(
        UUID.randomUUID().toString(),
        customerId,
        customerType,
        documentType,
        documentNumber,
        internalAccountNumber,
        externalAccountNumber,
        AccountType.CHECKING,
        AccountStatus.ACTIVE,
        Balance.zero("PEN"),
        Audit.create(),
        new HashSet<>(),
        BigDecimal.valueOf(5),
        LocalDate.now().plusMonths(1).withDayOfMonth(1),
        new HashSet<>(),
        TransactionLimit.of());
  }

  public static CheckingAccount reconstruct(
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
      BigDecimal maintenanceFee,
      LocalDate nextFeeDate,
      Set<AuthorizedSigner> signers,
      TransactionLimit transactionLimit) {
    return new CheckingAccount(
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
        maintenanceFee,
        nextFeeDate,
        signers,
        transactionLimit);
  }
  
  /**
   * Aplica el cobro de mantenimiento o suspende la cuenta si no hay fondos suficientes.
   * Este método maneja la lógica específica del cobro de mantenimiento.
   * @return nueva instancia de cuenta con el mantenimiento aplicado o suspendida
   */
  public Account processMaintenanceFeePayment() {

    if (!isMaintenanceFeeDue()) {
      throw new IllegalStateException("Maintenance fee is not due yet");
    }

    // Si no puede pagar el mantenimiento, suspender la cuenta
    if (!canPayMaintenanceFee()) {
      return this.changeStatus(AccountStatus.SUSPENDED);
    }

    // Si puede pagar, aplicar el cobro
    Balance newBalance = this.balance.update(this.maintenanceFee.negate());
    LocalDate nextFeeDate = this.nextFeeDate.plusMonths(1);
    return new CheckingAccount(
        this.getIdValue(),
        this.customerId,
        this.customerType,
        this.getDocumentType(),
        this.getDocumentNumber(),
        this.getAccountNumber(),
        this.getExternalAccountNumber(),
        this.accountType,
        this.status, // Mantener el estado actual (ACTIVE)
        newBalance,
        this.audit.update(),
        this.holders,
        this.maintenanceFee,
        nextFeeDate,
        this.signers,
        this.transactionLimit
    );
  }


  @Override
  protected void validateBusinessRules() {
    if (this.isPersonalAccount() && this.holders.size() > 1)
      throw new IllegalArgumentException("Personal checking accounts cannot have multiple holders");
    if (this.isPersonalAccount() && !this.signers.isEmpty())
      throw new IllegalArgumentException(
          "Personal checking accounts cannot have authorized signers");
    if (this.maintenanceFee.compareTo(BigDecimal.ZERO) < 0)
      throw new IllegalArgumentException("Maintenance fee cannot be negative");
    if (this.nextFeeDate.isBefore(LocalDate.now().withDayOfMonth(1)))
      throw new IllegalArgumentException("Next fee date cannot be in the past");
  }

  @Override
  public Account changeStatus(AccountStatus status) {
    if (this.getStatus().equals(status))
      throw new IllegalArgumentException("Account status cannot be the same");
    return new CheckingAccount(
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
        this.maintenanceFee,
        this.nextFeeDate,
        this.signers,
        this.transactionLimit);
  }

  @Override
  protected void canPerformTransactionSpecific(OperationDirection direction, BigDecimal amount) {
    // This method is not used for checking accounts
  }

  @Override
  public Account addHolder(AccountHolder newHolder) {
    if (newHolder.isPrimaryHolder())
      throw new IllegalArgumentException("Cannot add primary holder, it's already set");
    validateTotalParticipation(newHolder.getParticipationPercentage());
    this.holders.add(newHolder);
    Set<AccountHolder> redistributedHolders = redistributePercentages();
    return new CheckingAccount(
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
        redistributedHolders,
        this.maintenanceFee,
        this.nextFeeDate,
        this.signers,
        this.transactionLimit);
  }

  private void validateTotalParticipation(BigDecimal newPercentage) {
    if (newPercentage == null)
      throw new IllegalArgumentException("Participation percentage cannot be null");

    if (newPercentage.compareTo(BigDecimal.ZERO) < 0
        || newPercentage.compareTo(BigDecimal.valueOf(100)) > 0)
      throw new IllegalArgumentException("Participation percentage must be between 0% and 100%");
  }

  private Set<AccountHolder> redistributePercentages() {
    List<AccountHolder> secondaryHolders =
        this.holders.stream()
            .filter(holder -> !holder.isPrimaryHolder())
            .collect(Collectors.toList());

    if (secondaryHolders.isEmpty())
      throw new IllegalArgumentException(
          "Cannot redistribute percentages, there are no secondary holders");

    BigDecimal totalSecondaryPercentage =
        secondaryHolders.stream()
            .map(AccountHolder::getParticipationPercentage)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal remainingForPrimary = BigDecimal.valueOf(100).subtract(totalSecondaryPercentage);
    return updatePrimaryHolderPercentage(remainingForPrimary);
  }

  private Set<AccountHolder> updatePrimaryHolderPercentage(BigDecimal newPercentage) {
    AccountHolder primaryHolder = this.getPrimaryHolder();
    AccountHolder updatedPrimaryHolder =
        AccountHolder.ofPrimaryHolder(
            primaryHolder.getDocumentType(), primaryHolder.getDocumentNumber(), newPercentage);
    this.holders.remove(primaryHolder);
    this.holders.add(updatedPrimaryHolder);
    return this.holders;
  }

  @Override
  public Account removeHolder(AccountHolder accountHolder) {

    if (accountHolder.isPrimaryHolder())
      throw new IllegalArgumentException("Primary holder cannot be removed");

    AccountHolder holderToRemove =
        this.holders.stream()
            .filter(holder -> holder.equals(accountHolder))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Holder not found"));

    BigDecimal removedPercentage = holderToRemove.getParticipationPercentage();

    this.holders.remove(holderToRemove);

    BigDecimal totalRemaining =
        this.holders.stream()
            .map(AccountHolder::getParticipationPercentage)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    Set<AccountHolder> updatedHolders = new HashSet<>();

    for (AccountHolder holder : this.holders) {
      BigDecimal share =
          holder.getParticipationPercentage().divide(totalRemaining, 10, RoundingMode.HALF_UP);
      BigDecimal extra = removedPercentage.multiply(share);
      BigDecimal newPercentage = holder.getParticipationPercentage().add(extra);
      if (holder.isPrimaryHolder()) {
        AccountHolder updatedPrimaryHolder =
            AccountHolder.ofPrimaryHolder(
                holder.getDocumentType(), holder.getDocumentNumber(), newPercentage);
        updatedHolders.add(updatedPrimaryHolder);
      } else {
        AccountHolder updatedSecondaryHolder =
            AccountHolder.ofSecondaryHolder(
                holder.getDocumentType(), holder.getDocumentNumber(), newPercentage);
        updatedHolders.add(updatedSecondaryHolder);
      }
    }
    this.holders.clear();
    return new CheckingAccount(
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
        updatedHolders,
        this.maintenanceFee,
        this.nextFeeDate,
        this.signers,
        this.transactionLimit);
  }

  @Override
  public Account withNewBalance(Balance newBalance) {
    return new CheckingAccount(
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
        this.maintenanceFee,
        this.nextFeeDate,
        this.signers,
        this.transactionLimit);
  }

  @Override
  public Account withNewTransactionLimit(TransactionLimit newTransactionLimit) {
    return new CheckingAccount(
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
        this.maintenanceFee,
        this.nextFeeDate,
        this.signers,
        newTransactionLimit);
  }

  @Override
  public Account updateOperationDateIfNeeded() {
    return this;
  }

  public boolean isMaintenanceFeeDue() {
    return LocalDate.now().isAfter(this.nextFeeDate);
  }

  public boolean canPayMaintenanceFee() {
    return this.balance.getAmount().compareTo(this.maintenanceFee) >= 0;
  }

  public boolean hasSigners() {
    return !signers.isEmpty();
  }

  public CheckingAccount addSigner(AuthorizedSigner signer) {
    if (!this.isEnterpriseAccount())
      throw new IllegalStateException("Only enterprise accounts can have signers");
    Set<AuthorizedSigner> newSigners = new HashSet<>(this.signers);
    newSigners.add(signer);
    return new CheckingAccount(
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
        this.maintenanceFee,
        this.nextFeeDate,
        newSigners,
        this.transactionLimit);
  }

  public CheckingAccount removeSigner(String documentNumber) {
    if (!this.isEnterpriseAccount())
      throw new IllegalStateException("Only enterprise accounts can have signers");
    Set<AuthorizedSigner> newSigners = new HashSet<>(this.signers);
    newSigners.removeIf(signer -> signer.getDocumentNumber().equals(documentNumber));
    return new CheckingAccount(
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
        this.maintenanceFee,
        this.nextFeeDate,
        newSigners,
        this.transactionLimit);
  }

  public boolean isMultiHolderAccount() {
    return this.holders.size() > 1;
  }

  public Set<AccountHolder> getSecondaryHolders() {
    return this.holders.stream()
        .filter(holder -> !holder.isPrimaryHolder())
        .collect(Collectors.toSet());
  }
}
