package com.NTTDATA.bootcamp.msvc_account.domain;

import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountStatus;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountType;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.OperationType;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    protected Account(String id, String customerId, String customerType, String documentType, String documentNumber, String accountNumber, String externalAccountNumber, AccountType accountType, AccountStatus status, Balance balance, Audit audit, Set<AccountHolder> holders) {
        this.id = AccountId.of(id);
        this.customerId = customerId;
        this.customerType = customerType;
        this.accountNumber = InternalAccountNumber.of(accountNumber);
        this.externalAccountNumber = ExternalAccountNumber.of(externalAccountNumber);
        this.accountType = accountType;
        this.status = status;
        this.balance = balance;
        this.audit = audit;
        this.holders = holders;

        this.holders.add(AccountHolder.ofPrimaryHolder(documentType, documentNumber));
    }

    public final boolean canPerformTransaction(OperationType operation, BigDecimal amount) {
        if(!this.isActive()) return false;
        if(operation == OperationType.WITHDRAWAL && !balance.hasSufficientFunds(amount)) return false;
        return this.canPerformTransactionSpecific(operation, amount);
    }

    /*METODOS ABSTRACTOS*/
    protected abstract void validateBusinessRules();
    protected abstract boolean canPerformTransactionSpecific(OperationType operation, BigDecimal amount);
    protected abstract Account updateBalance(BigDecimal amount, OperationType operation);
    protected abstract Account recordTransaction();
    protected abstract Account suspend();
    protected abstract Account activate();
    protected abstract Account close();
    protected abstract Account block();
    protected abstract Account addHolder(AccountHolder newHolder);
    protected abstract Account removeHolder(AccountHolder accountHolder);

    /*METODOS GET DE VO*/
    public String getIdValue() {
        return this.id.getValue();
    }

    public String getDocumentType() {
        return this.getPrimaryHolder().getDocumentType();
    }

    public String getDocumentNumber() {
        return this.getPrimaryHolder().getDocumentNumber();
    }

    public String getAccountNumber() {
        return this.accountNumber.getValue();
    }

    public String getExternalAccountNumber() {
        return this.externalAccountNumber.getValue();
    }
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

    public boolean canReceiveTransactions() {
        return this.isActive() && !this.isBlocked();
    }

    public boolean hasSufficientFunds() {
        return this.balance.isPositive();
    }

    public AccountHolder getPrimaryHolder() {
        return this.holders.stream()
                .filter(AccountHolder::isPrimaryHolder)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Primary holder not found"));
    }

    /*METODOS DE MODIFICACIÓN (FALTA REFACTORIZAR PARA RETORNAR UN NUEVO OBJETO)*/
    /**
    * @param newPercentages key documentNumber, value percentage
    * */
    public void updateAllPercentages(Map<String, BigDecimal> newPercentages) {
        validateTotalEquals100(newPercentages);
        validateAllHoldersExist(newPercentages);
        updateAllHoldersPercentages(newPercentages);
    }

    private void validateTotalEquals100(Map<String, BigDecimal> newPercentages) {
        BigDecimal total = newPercentages.values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(total.compareTo(BigDecimal.valueOf(100)) != 0) throw new IllegalArgumentException("Total percentage must be 100%");
    }

    private void validateAllHoldersExist(Map<String, BigDecimal> newPercentages) {
        Set<String> documentNumbersFromMap = newPercentages.keySet();

        Set<String> documentNumbersFromAccount = this.holders.stream()
                .map(AccountHolder::getDocumentNumber)
                .collect(Collectors.toSet());

        if(!documentNumbersFromAccount.equals(documentNumbersFromMap)) throw new IllegalArgumentException("All holders must exist in the account") ;
    }

    private void updateAllHoldersPercentages(Map<String, BigDecimal> newPercentages) {
        Set<AccountHolder> updatedHolders = new HashSet<>();

        for (AccountHolder existingHolder : this.holders) {
            String documentNumber = existingHolder.getDocumentNumber();
            BigDecimal newPercentage = newPercentages.get(documentNumber);

            if(existingHolder.isPrimaryHolder()) {
                AccountHolder updatedPrimaryHolder = AccountHolder.ofPrimaryHolder(existingHolder.getDocumentType(), documentNumber, newPercentage);
                updatedHolders.add(updatedPrimaryHolder);
            } else {
                AccountHolder updatedHolder = AccountHolder.ofSecondaryHolder(existingHolder.getDocumentType(), documentNumber, newPercentage);
                updatedHolders.add(updatedHolder);
            }
        }
        this.holders.clear();
        this.holders.addAll(updatedHolders);
    }

}
