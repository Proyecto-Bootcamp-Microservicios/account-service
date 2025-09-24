package com.ntt.data.bootcamp.msvc.account.domain;

import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountStatus;
import com.ntt.data.bootcamp.msvc.account.domain.enums.AccountType;
import com.ntt.data.bootcamp.msvc.account.domain.util.AccountNumberGenerator;
import com.ntt.data.bootcamp.msvc.account.domain.vo.AccountHolder;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Audit;
import com.ntt.data.bootcamp.msvc.account.domain.vo.AuthorizedSigner;
import com.ntt.data.bootcamp.msvc.account.domain.vo.Balance;
import com.ntt.data.bootcamp.msvc.account.domain.vo.TransactionLimit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class PymeCheckingAccount extends CheckingAccount {

  private PymeCheckingAccount(
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
        maintenanceFee,
        nextFeeDate,
        signers,
        transactionLimit);
    validateBusinessRules();
  }

  public static PymeCheckingAccount of(
      String customerId, String customerType, String documentType, String documentNumber) {
    String internalAccountNumber = AccountNumberGenerator.generateInternal();
    String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);

    return new PymeCheckingAccount(
        UUID.randomUUID().toString(),
        customerId,
        customerType,
        documentType,
        documentNumber,
        internalAccountNumber,
        externalAccountNumber,
        AccountType.PYME_CHECKING,
        AccountStatus.ACTIVE,
        Balance.zero("PEN"),
        Audit.create(),
        new HashSet<>(),
        BigDecimal.ZERO,
        LocalDate.now().plusMonths(1).withDayOfMonth(1),
        new HashSet<>(),
        TransactionLimit.of());
  }

  public static PymeCheckingAccount reconstruct(
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
    return new PymeCheckingAccount(
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

  @Override
  protected void validateBusinessRules() {
    super.validateBusinessRules(); // Llamar validaciones de CheckingAccount

    // Validación específica PYME: sin comisión de mantenimiento
    if (this.maintenanceFee.compareTo(BigDecimal.ZERO) != 0) {
      throw new IllegalArgumentException("PYME checking accounts must have zero maintenance fee");
    }

    // Validación específica PYME: solo para empresas
    if (!this.isEnterpriseAccount()) {
      throw new IllegalArgumentException("PYME checking accounts are only for enterprise customers");
    }
  }

  @Override
  public Account changeStatus(AccountStatus status) {
    if (this.getStatus().equals(status)) {
      throw new IllegalArgumentException("Account status cannot be the same");
    }
    return new PymeCheckingAccount(
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
  public Account addHolder(AccountHolder newHolder) {
    if (newHolder.isPrimaryHolder()) {
      throw new IllegalArgumentException("Cannot add primary holder, it's already set");
    }
    validateTotalParticipation(newHolder.getParticipationPercentage());
    this.holders.add(newHolder);
    Set<AccountHolder> redistributedHolders = redistributePercentages();
    return new PymeCheckingAccount(
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

  @Override
  public Account removeHolder(AccountHolder accountHolder) {
    if (accountHolder.isPrimaryHolder()) {
      throw new IllegalArgumentException("Primary holder cannot be removed");
    }

    AccountHolder holderToRemove = this.holders.stream()
        .filter(holder -> holder.equals(accountHolder))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Holder not found"));

    BigDecimal removedPercentage = holderToRemove.getParticipationPercentage();
    this.holders.remove(holderToRemove);

    BigDecimal totalRemaining = this.holders.stream()
        .map(AccountHolder::getParticipationPercentage)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    Set<AccountHolder> updatedHolders = new HashSet<>();

    for (AccountHolder holder : this.holders) {
      BigDecimal share = holder.getParticipationPercentage().divide(totalRemaining, 10, RoundingMode.HALF_UP);
      BigDecimal extra = removedPercentage.multiply(share);
      BigDecimal newPercentage = holder.getParticipationPercentage().add(extra);
      if (holder.isPrimaryHolder()) {
        AccountHolder updatedPrimaryHolder = AccountHolder.ofPrimaryHolder(
            holder.getDocumentType(), holder.getDocumentNumber(), newPercentage);
        updatedHolders.add(updatedPrimaryHolder);
      } else {
        AccountHolder updatedSecondaryHolder = AccountHolder.ofSecondaryHolder(
            holder.getDocumentType(), holder.getDocumentNumber(), newPercentage);
        updatedHolders.add(updatedSecondaryHolder);
      }
    }
    this.holders.clear();
    return new PymeCheckingAccount(
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
    return new PymeCheckingAccount(
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
    return new PymeCheckingAccount(
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

  public PymeCheckingAccount addSigner(AuthorizedSigner signer) {
    if (!this.isEnterpriseAccount()) {
      throw new IllegalStateException("Only enterprise accounts can have signers");
    }
    Set<AuthorizedSigner> newSigners = new HashSet<>(this.signers);
    newSigners.add(signer);
    return new PymeCheckingAccount(
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

  public PymeCheckingAccount removeSigner(String documentNumber) {
    if (!this.isEnterpriseAccount()) {
      throw new IllegalStateException("Only enterprise accounts can have signers");
    }
    Set<AuthorizedSigner> newSigners = new HashSet<>(this.signers);
    newSigners.removeIf(signer -> signer.getDocumentNumber().equals(documentNumber));
    return new PymeCheckingAccount(
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

  // Métodos específicos PYME
  public boolean hasNoMaintenanceFee() {
    return this.maintenanceFee.compareTo(BigDecimal.ZERO) == 0;
  }

  public boolean isPymeAccount() {
    return true;
  }

  private void validateTotalParticipation(BigDecimal newPercentage) {
    if (newPercentage == null) {
      throw new IllegalArgumentException("Participation percentage cannot be null");
    }
    if (newPercentage.compareTo(BigDecimal.ZERO) < 0 || newPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
      throw new IllegalArgumentException("Participation percentage must be between 0% and 100%");
    }
  }

  private Set<AccountHolder> redistributePercentages() {
    List<AccountHolder> secondaryHolders = this.holders.stream()
        .filter(holder -> !holder.isPrimaryHolder())
        .collect(Collectors.toList());

    if (secondaryHolders.isEmpty()) {
      throw new IllegalArgumentException("Cannot redistribute percentages, there are no secondary holders");
    }

    BigDecimal totalSecondaryPercentage = secondaryHolders.stream()
        .map(AccountHolder::getParticipationPercentage)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal remainingForPrimary = BigDecimal.valueOf(100).subtract(totalSecondaryPercentage);
    return updatePrimaryHolderPercentage(remainingForPrimary);
  }

  private Set<AccountHolder> updatePrimaryHolderPercentage(BigDecimal newPercentage) {
    AccountHolder primaryHolder = this.getPrimaryHolder();
    AccountHolder updatedPrimaryHolder = AccountHolder.ofPrimaryHolder(
        primaryHolder.getDocumentType(), primaryHolder.getDocumentNumber(), newPercentage);
    this.holders.remove(primaryHolder);
    this.holders.add(updatedPrimaryHolder);
    return this.holders;
  }
}