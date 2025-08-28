package com.NTTDATA.bootcamp.msvc_account.domain;

import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountStatus;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountType;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.OperationType;
import com.NTTDATA.bootcamp.msvc_account.domain.util.AccountNumberGenerator;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.AccountHolder;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.Audit;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.Balance;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.MonthlyMovementLimit;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class SavingsAccount extends Account {
    private final MonthlyMovementLimit monthlyMovementLimit;

    private SavingsAccount(String id, String customerId, String customerType, String documentType, String documentNumber, String accountNumber, String externalAccountNumber, AccountType accountType, AccountStatus status, Balance balance, Audit audit, Set<AccountHolder> holders, MonthlyMovementLimit monthlyMovementLimit) {
        super(id, customerId, customerType, documentType, documentNumber, accountNumber, externalAccountNumber, accountType, status, balance, audit, holders);
        this.monthlyMovementLimit = monthlyMovementLimit;
        validateBusinessRules();
    }

    public static SavingsAccount of(String customerId, String customerType, String documentType, String documentNumber){
        String internalAccountNumber = AccountNumberGenerator.generateInternal();
        String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);
        return new SavingsAccount(UUID.randomUUID().toString(), customerId, customerType, documentType, documentNumber,
                internalAccountNumber, externalAccountNumber, AccountType.SAVINGS, AccountStatus.ACTIVE,
                Balance.zero("PEN"), Audit.create(), new HashSet<>(), MonthlyMovementLimit.of(10));
    }

    public static SavingsAccount reconstruct(String id, String customerId, String customerType, String documentType, String documentNumber, String accountNumber, String externalAccountNumber, AccountType accountType, AccountStatus status, Balance balance, Audit audit, Set<AccountHolder> holders, MonthlyMovementLimit monthlyMovementLimit) {
        return new SavingsAccount(id, customerId, customerType, documentType, documentNumber, accountNumber, externalAccountNumber, accountType, status, balance, audit, holders, monthlyMovementLimit);
    }

    @Override
    protected void validateBusinessRules() {
        if(isEnterpriseAccount()) throw new IllegalArgumentException("Enterprise accounts are not allowed");
        if(this.balance == null) throw new IllegalArgumentException("Balance cannot be null");
        if(this.holders == null || this.holders.isEmpty()) throw new IllegalArgumentException("Account must have at least one holder");
        if(this.monthlyMovementLimit == null) throw new IllegalArgumentException("Monthly movement limit cannot be null");
    }

    @Override
    protected boolean canPerformTransactionSpecific(OperationType operation, BigDecimal amount) {
        return !monthlyMovementLimit.isLimitReached();
    }

    @Override
    protected Account updateBalance(BigDecimal amount, OperationType operation) {
        if(!canPerformTransaction(operation, amount)) throw new IllegalArgumentException("Transaction not allowed");
        if(operation.equals(OperationType.DEPOSIT)) {
            Balance updatedBalance = this.balance.update(amount);
            return new SavingsAccount(this.id.getValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, this.status, updatedBalance, this.audit.update(), this.holders, this.monthlyMovementLimit);
        }
        if(operation.equals(OperationType.WITHDRAWAL) || operation.equals(OperationType.TRANSFER)) {
            Balance updatedBalance = this.balance.update(amount.negate());
            return new SavingsAccount(this.id.getValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, this.status, updatedBalance, this.audit.update(), this.holders, this.monthlyMovementLimit);
        }
        throw new IllegalArgumentException("Transaction not allowed");
    }

    public SavingsAccount recordTransaction() {
        MonthlyMovementLimit updatedLimit = monthlyMovementLimit.incrementMovements();

        return new SavingsAccount(
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
                updatedLimit
        );
    }

    @Override
    protected SavingsAccount suspend() {
        if(isSuspended()) throw new IllegalArgumentException("Account is already suspended");
        return new SavingsAccount(this.id.getValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.SUSPENDED, this.balance, this.audit.update(), this.holders, this.monthlyMovementLimit);
    }

    @Override
    protected SavingsAccount activate() {
        if(isActive()) throw new IllegalArgumentException("Account is already active");
        return new SavingsAccount(this.id.getValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.ACTIVE, this.balance, this.audit.update(), this.holders, this.monthlyMovementLimit);
    }

    @Override
    protected SavingsAccount close() {
        if(isClosed()) throw new IllegalArgumentException("Account is already closed");
        return new SavingsAccount(this.id.getValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.CLOSED, this.balance, this.audit.update(), this.holders, this.monthlyMovementLimit);
    }

    @Override
    protected SavingsAccount block() {
        if(isBlocked()) throw new IllegalArgumentException("Account is already blocked");
        return new SavingsAccount(this.id.getValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.BLOCKED, this.balance, this.audit.update(), this.holders, this.monthlyMovementLimit);
    }

    @Override
    public SavingsAccount addHolder(AccountHolder holder) {
        throw new UnsupportedOperationException("Savings accounts cannot have multiple holders");
    }

    @Override
    public SavingsAccount removeHolder(AccountHolder accountHolder) {
        throw new UnsupportedOperationException("Savings accounts cannot have multiple holders");
    }

}
