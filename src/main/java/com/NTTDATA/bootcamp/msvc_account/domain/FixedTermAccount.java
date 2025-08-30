package com.NTTDATA.bootcamp.msvc_account.domain;

import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountStatus;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountType;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.OperationType;
import com.NTTDATA.bootcamp.msvc_account.domain.util.AccountNumberGenerator;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.AccountHolder;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.Audit;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.Balance;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class FixedTermAccount extends Account {
    private final LocalDate maturityDate;
    private final LocalDate operationDate;
    private final BigDecimal interestRate;
    private final boolean hasPerformedMonthlyOperation;

    private FixedTermAccount(String id, String customerId, String customerType, String documentType, String documentNumber, String accountNumber, String externalAccountNumber, AccountType accountType, AccountStatus status, Balance balance, Audit audit, Set<AccountHolder> holders, LocalDate maturityDate, LocalDate operationDate, BigDecimal interestRate, boolean hasPerformedMonthlyOperation) {
        super(id, customerId, customerType, documentType, documentNumber, accountNumber, externalAccountNumber, accountType, status, balance, audit, holders);

        if(isEnterpriseAccount()) throw new IllegalArgumentException("Enterprise accounts are not allowed");

        this.maturityDate = maturityDate;
        this.operationDate = operationDate;
        this.interestRate = interestRate;
        this.hasPerformedMonthlyOperation = hasPerformedMonthlyOperation;
        validateFixedTermRules();
    }

    public static FixedTermAccount ofSemiAnnually(String customerId, String customerType,
                                      String documentType, String documentNumber) {

        String internalAccountNumber = AccountNumberGenerator.generateInternal();
        String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);
        LocalDate maturityDate = LocalDate.now().plusYears(1).withDayOfMonth(1);

        return new FixedTermAccount(UUID.randomUUID().toString(), customerId, customerType,
                documentType, documentNumber, internalAccountNumber, externalAccountNumber,
                AccountType.FIXED_TERM, AccountStatus.ACTIVE,
                Balance.zero("PEN"), Audit.create(), new HashSet<>(),
                maturityDate, LocalDate.now().withDayOfMonth(15), BigDecimal.valueOf(0.02), false);
    }

    public static FixedTermAccount ofAnnually(String customerId, String customerType,
                                      String documentType, String documentNumber) {

        String internalAccountNumber = AccountNumberGenerator.generateInternal();
        String externalAccountNumber = AccountNumberGenerator.generateExternal(internalAccountNumber);
        LocalDate maturityDate = LocalDate.now().plusYears(1).withDayOfMonth(1);

        return new FixedTermAccount(UUID.randomUUID().toString(), customerId, customerType,
                documentType, documentNumber, internalAccountNumber, externalAccountNumber,
                AccountType.FIXED_TERM, AccountStatus.ACTIVE,
                Balance.zero("PEN"), Audit.create(), new HashSet<>(),
                maturityDate, LocalDate.now().withDayOfMonth(15), BigDecimal.valueOf(0.05), false);
    }

    public static FixedTermAccount reconstruct(String id, String customerId, String customerType,
                                      String documentType, String documentNumber, String accountNumber, String externalAccountNumber, AccountType accountType, AccountStatus status, Balance balance, Audit audit, Set<AccountHolder> holders, LocalDate maturityDate, LocalDate operationDate, BigDecimal interestRate, boolean hasPerformedMonthlyOperation) {
        return new FixedTermAccount(id, customerId, customerType, documentType, documentNumber, accountNumber, externalAccountNumber, accountType, status, balance, audit, holders, maturityDate, operationDate, interestRate, hasPerformedMonthlyOperation);
    }

    @Override
    protected void validateBusinessRules() {
        validateFixedTermRules();
    }

    @Override
    protected boolean canPerformTransactionSpecific(OperationType operation, BigDecimal amount) {
        return isOperationDate() && !hasPerformedMonthlyOperation;
    }

    @Override
    protected Account updateBalance(BigDecimal amount, OperationType operation) {
        if (!this.isOperationDate()) throw new IllegalStateException("Can only update balance on operation date");
        if (this.hasPerformedMonthlyOperation) throw new IllegalStateException("Monthly operation already performed");
        if (!this.isActive()) throw new IllegalStateException("Account must be active to update balance");
        BigDecimal newBalance = this.balance.getAmount().add(amount);
        if (newBalance.compareTo(BigDecimal.valueOf(1000)) < 0) throw new IllegalStateException("Balance cannot go below minimum 1000");
        return new FixedTermAccount(
                this.id.getValue(),
                this.customerId,
                this.customerType,
                this.getPrimaryHolder().getDocumentType(),
                this.getPrimaryHolder().getDocumentNumber(),
                this.accountNumber.getValue(),
                this.externalAccountNumber.getValue(),
                this.accountType,
                this.status,
                Balance.of(this.balance.getCurrencyCode(), newBalance),
                this.audit.update(),
                this.holders,
                this.maturityDate,
                this.operationDate,
                this.interestRate,
                true
        );
    }

    public FixedTermAccount recordTransaction() {
        return new FixedTermAccount(
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
                this.maturityDate,
                this.operationDate,
                this.interestRate,
                true
        );
    }

    @Override
    protected FixedTermAccount suspend() {
        if(isSuspended()) throw new IllegalArgumentException("Account is already suspended");
        return new FixedTermAccount(this.id.getValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.SUSPENDED, this.balance, this.audit.update(), this.holders, this.maturityDate, this.operationDate, this.interestRate, this.hasPerformedMonthlyOperation);
    }

    @Override
    protected FixedTermAccount activate() {
        if(isActive()) throw new IllegalArgumentException("Account is already active");
        return new FixedTermAccount(this.id.getValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.ACTIVE, this.balance, this.audit.update(), this.holders, this.maturityDate, this.operationDate, this.interestRate, this.hasPerformedMonthlyOperation);
    }

    @Override
    protected FixedTermAccount close() {
        if(isClosed()) throw new IllegalArgumentException("Account is already closed");
        return new FixedTermAccount(this.id.getValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.CLOSED, this.balance, this.audit.update(), this.holders, this.maturityDate, this.operationDate, this.interestRate, this.hasPerformedMonthlyOperation);
    }

    @Override
    protected FixedTermAccount block() {
        if(isBlocked()) throw new IllegalArgumentException("Account is already blocked");
        return new FixedTermAccount(this.id.getValue(), this.customerId, this.customerType, this.getDocumentType(), this.getDocumentNumber(), this.getAccountNumber(), this.getExternalAccountNumber(), this.accountType, AccountStatus.BLOCKED, this.balance, this.audit.update(), this.holders, this.maturityDate, this.operationDate, this.interestRate, this.hasPerformedMonthlyOperation);
    }

    private void validateFixedTermRules() {
        if (!this.isPersonalAccount()) throw new IllegalArgumentException("Fixed term accounts can only be created for personal customers");
        if (maturityDate.isBefore(LocalDate.now())) throw new IllegalArgumentException("Maturity date must be in the future");
        if (this.balance.getAmount().compareTo(BigDecimal.valueOf(1000)) < 0) throw new IllegalArgumentException("Fixed term account requires minimum initial balance of 1000");
        if (interestRate.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Interest rate must be positive");
    }

    public boolean isOperationDate() {
        LocalDate today = LocalDate.now();
        return today.getDayOfMonth() == operationDate.getDayOfMonth();
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

}
