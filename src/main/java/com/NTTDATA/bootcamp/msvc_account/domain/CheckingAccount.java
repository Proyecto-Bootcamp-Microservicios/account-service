package com.NTTDATA.bootcamp.msvc_account.domain;

import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountStatus;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountType;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.OperationType;
import com.NTTDATA.bootcamp.msvc_account.domain.util.AccountNumberGenerator;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.AccountHolder;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.Audit;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.AuthorizedSigner;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.Balance;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public final class CheckingAccount extends Account {
    private final BigDecimal maintenanceFee;
    private final LocalDate nextFeeDate;
    private final Set<AuthorizedSigner> signers;

    private CheckingAccount(String id, String customerId, String customerType, String documentType, String documentNumber, String accountNumber, String externalAccountNumber, AccountType accountType, AccountStatus status, Balance balance, Audit audit, Set<AccountHolder> holders, BigDecimal maintenanceFee, LocalDate nextFeeDate, Set<AuthorizedSigner> signers) {
        super(id, customerId, customerType, documentType, documentNumber, accountNumber, externalAccountNumber, accountType, status, balance, audit, holders);
        this.maintenanceFee = maintenanceFee;
        this.nextFeeDate = nextFeeDate;
        this.signers = signers;
        validateBusinessRules();
    }

    public static CheckingAccount of(String customerId, String customerType, String documentType, String documentNumber) {

        String internalAccountNumber = AccountNumberGenerator.generateInternal();
        String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);

        return new CheckingAccount(UUID.randomUUID().toString(), customerId, customerType,
                documentType, documentNumber, internalAccountNumber, externalAccountNumber,
                AccountType.CHECKING, AccountStatus.ACTIVE, Balance.zero("PEN"), Audit.create(), new HashSet<>(),
                BigDecimal.valueOf(0), LocalDate.now().withDayOfMonth(1), new HashSet<>());
    }

    public static CheckingAccount of(String customerId, String customerType, String documentType, String documentNumber, BigDecimal amount) {

        String internalAccountNumber = AccountNumberGenerator.generateInternal();
        String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);

        return new CheckingAccount(UUID.randomUUID().toString(), customerId, customerType,
                documentType, documentNumber, internalAccountNumber, externalAccountNumber,
                AccountType.CHECKING, AccountStatus.ACTIVE, Balance.of("PEN", amount), Audit.create(), new HashSet<>(),
                BigDecimal.valueOf(0), LocalDate.now().withDayOfMonth(1), new HashSet<>());
    }

    public static CheckingAccount reconstruct(String id, String customerId, String customerType, String documentType, String documentNumber, String accountNumber, String externalAccountNumber, AccountType accountType, AccountStatus status, Balance balance, Audit audit, Set<AccountHolder> holders, BigDecimal maintenanceFee, LocalDate nextFeeDate, Set<AuthorizedSigner> signers) {
        return new CheckingAccount(id, customerId, customerType, documentType, documentNumber, accountNumber, externalAccountNumber, accountType, status, balance, audit, holders, maintenanceFee, nextFeeDate, signers);
    }

    @Override
    protected void validateBusinessRules() {
        if (this.isPersonalAccount() && this.holders.size() > 1) throw new IllegalArgumentException("Personal checking accounts cannot have multiple holders");
        if (this.maintenanceFee.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Maintenance fee cannot be negative");
        if (this.nextFeeDate.isBefore(LocalDate.now().withDayOfMonth(1))) throw new IllegalArgumentException("Next fee date cannot be in the past");
    }

    @Override
    protected boolean canPerformTransactionSpecific(OperationType operation, BigDecimal amount) {
        return true;
    }

    @Override
    protected Account updateBalance(BigDecimal amount, OperationType operation) {
        if(!canPerformTransaction(operation, amount)) throw new IllegalArgumentException("Transaction is not allowed");
        if(operation.equals(OperationType.DEPOSIT)){
            Balance newBalance = this.balance.update(amount);
            return new CheckingAccount(this.getIdValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, this.status, newBalance, this.audit.update(), this.holders, this.maintenanceFee, this.nextFeeDate, this.signers);
        }
        if(operation.equals(OperationType.WITHDRAWAL) || operation.equals(OperationType.TRANSFER)){
            Balance newBalance = this.balance.update(amount.negate());
            return new CheckingAccount(this.getIdValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, this.status, newBalance, this.audit.update(), this.holders, this.maintenanceFee, this.nextFeeDate, this.signers);
        }
        throw new IllegalArgumentException("Transaction is not allowed");
    }

    @Override
    protected CheckingAccount recordTransaction() {
        return new CheckingAccount(
                this.id.getValue(),
                this.customerId,
                this.customerType,
                this.getPrimaryHolder().getDocumentType(),
                this.getPrimaryHolder().getDocumentNumber(),
                this.accountNumber.getValue(),
                this.externalAccountNumber.getValue(),
                this.accountType,
                this.status,
                this.balance,
                this.audit.update(),
                this.holders,
                this.maintenanceFee,
                this.nextFeeDate,
                this.signers
        );
    }


    @Override
    protected CheckingAccount suspend() {
        if(isSuspended()) throw new IllegalArgumentException("Account is already suspended");
        return new CheckingAccount(this.getIdValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.SUSPENDED, this.balance, this.audit.update(), this.holders, this.maintenanceFee, this.nextFeeDate, this.signers);
    }

    @Override
    protected CheckingAccount activate() {
        if(isActive()) throw new IllegalArgumentException("Account is already active");
        return new CheckingAccount(this.getIdValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.ACTIVE, this.balance, this.audit.update(), this.holders, this.maintenanceFee, this.nextFeeDate, this.signers);
    }

    @Override
    protected CheckingAccount close() {
        if(isClosed()) throw new IllegalArgumentException("Account is already closed");
        return new CheckingAccount(this.getIdValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.CLOSED, this.balance, this.audit.update(), this.holders, this.maintenanceFee, this.nextFeeDate, this.signers);
    }

    @Override
    protected CheckingAccount block() {
        if(isBlocked()) throw new IllegalArgumentException("Account is already blocked");
        return new CheckingAccount(this.getIdValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.BLOCKED, this.balance, this.audit.update(), this.holders, this.maintenanceFee, this.nextFeeDate, this.signers);
    }

    @Override
    protected Account addHolder(AccountHolder newHolder) {
        if (newHolder.isPrimaryHolder()) throw new IllegalArgumentException("Cannot add primary holder, it's already set");
        validateTotalParticipation(newHolder.getParticipationPercentage());
        this.holders.add(newHolder);
        Set<AccountHolder> redistributedHolders = redistributePercentages();
        return new CheckingAccount(this.getIdValue(), this.customerId, this.customerType, this.getDocumentType(),
                this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType,
                this.status, this.balance, this.audit.update(), redistributedHolders, this.maintenanceFee, this.nextFeeDate, this.signers);
    }

    private void validateTotalParticipation(BigDecimal newPercentage) {
        if (newPercentage == null) throw new IllegalArgumentException("Participation percentage cannot be null");

        if (newPercentage.compareTo(BigDecimal.ZERO) < 0 ||
                newPercentage.compareTo(BigDecimal.valueOf(100)) > 0) throw new IllegalArgumentException("Participation percentage must be between 0% and 100%");
    }

    private Set<AccountHolder> redistributePercentages() {
        List<AccountHolder> secondaryHolders = this.holders.stream()
                .filter(holder -> !holder.isPrimaryHolder())
                .collect(Collectors.toList());

        if (secondaryHolders.isEmpty()) throw new IllegalArgumentException("Cannot redistribute percentages, there are no secondary holders");

        BigDecimal totalSecondaryPercentage = secondaryHolders.stream()
                .map(AccountHolder::getParticipationPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingForPrimary = BigDecimal.valueOf(100).subtract(totalSecondaryPercentage);
        return updatePrimaryHolderPercentage(remainingForPrimary);
    }

    private Set<AccountHolder> updatePrimaryHolderPercentage(BigDecimal newPercentage) {
        AccountHolder primaryHolder = this.getPrimaryHolder();
        AccountHolder updatedPrimaryHolder = AccountHolder.ofPrimaryHolder(primaryHolder.getDocumentType(), primaryHolder.getDocumentNumber(), newPercentage);
        this.holders.remove(primaryHolder);
        this.holders.add(updatedPrimaryHolder);
        return this.holders;
    }

    @Override
    public Account removeHolder(AccountHolder accountHolder) {

        if(accountHolder.isPrimaryHolder()) throw new IllegalArgumentException("Primary holder cannot be removed");

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
            BigDecimal share = holder.getParticipationPercentage()
                    .divide(totalRemaining, 10, RoundingMode.HALF_UP); // proporción
            BigDecimal extra = removedPercentage.multiply(share);
            BigDecimal newPercentage = holder.getParticipationPercentage().add(extra);
            if(holder.isPrimaryHolder()){
                AccountHolder updatedPrimaryHolder = AccountHolder.ofPrimaryHolder(holder.getDocumentType(), holder.getDocumentNumber(), newPercentage);
                updatedHolders.add(updatedPrimaryHolder);
            } else{
                AccountHolder updatedSecondaryHolder = AccountHolder.ofSecondaryHolder(holder.getDocumentType(), holder.getDocumentNumber(), newPercentage);
                updatedHolders.add(updatedSecondaryHolder);
            }
        }
        this.holders.clear();
        return new CheckingAccount(this.getIdValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, this.status, this.balance, this.audit.update(), updatedHolders, this.maintenanceFee, this.nextFeeDate, this.signers);
    }

    public boolean isMaintenanceFeeDue() {
        return LocalDate.now().isAfter(this.nextFeeDate);
    }

    public boolean canPayMaintenanceFee() {
        return this.balance.getAmount().compareTo(this.maintenanceFee) >= 0;
    }

    public Set<AuthorizedSigner> getSigners() {
        return signers;
    }

    public boolean hasSigners() {
        return !signers.isEmpty();
    }

    // Métodos para gestionar firmantes (solo para cuentas empresariales)
    public CheckingAccount addSigner(AuthorizedSigner signer) {
        if (!this.isEnterpriseAccount()) {
            throw new IllegalStateException("Only enterprise accounts can have signers");
        }
        Set<AuthorizedSigner> newSigners = new HashSet<>(this.signers);
        newSigners.add(signer);
        return new CheckingAccount(this.getIdValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, this.status, this.balance, this.audit.update(), this.holders, this.maintenanceFee, this.nextFeeDate, newSigners);
    }

    public CheckingAccount removeSigner(String documentNumber) {
        if (!this.isEnterpriseAccount()) {
            throw new IllegalStateException("Only enterprise accounts can have signers");
        }
        Set<AuthorizedSigner> newSigners = new HashSet<>(this.signers);
        newSigners.removeIf(signer -> signer.getDocumentNumber().equals(documentNumber));
        return new CheckingAccount(this.getIdValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, this.status, this.balance, this.audit.update(), this.holders, this.maintenanceFee, this.nextFeeDate, newSigners);
    }

    // PERMITE múltiples titulares (para cuentas empresariales)
    public boolean isMultiHolderAccount() {
        return this.holders.size() > 1;
    }

    public Set<AccountHolder> getSecondaryHolders() {
        return this.holders.stream()
                .filter(holder -> !holder.isPrimaryHolder())
                .collect(Collectors.toSet());
    }
}
