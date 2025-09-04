package com.NTTDATA.bootcamp.msvc_account.domain;

import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountStatus;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountType;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.OperationType;
import com.NTTDATA.bootcamp.msvc_account.domain.util.AccountNumberGenerator;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.AccountHolder;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.Audit;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.Balance;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.TransactionLimit;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class FixedTermAccount extends Account {
    private final LocalDate maturityDate;
    private final int dayOfOperation;
    private final BigDecimal interestRate;
    private final boolean hasPerformedMonthlyOperation;

    private FixedTermAccount(String id, String customerId, String customerType,
                             String documentType, String documentNumber,
                             String accountNumber, String externalAccountNumber, AccountType accountType,
                             AccountStatus status, Balance balance, Audit audit, Set<AccountHolder> holders,
                             LocalDate maturityDate, int dayOfOperation, BigDecimal interestRate,
                             boolean hasPerformedMonthlyOperation, TransactionLimit transactionLimit) {
        super(id, customerId, customerType, documentType, documentNumber, accountNumber, externalAccountNumber, accountType, status, balance, audit, holders, transactionLimit);

        this.maturityDate = maturityDate;
        this.dayOfOperation = dayOfOperation;
        this.interestRate = interestRate;
        this.hasPerformedMonthlyOperation = hasPerformedMonthlyOperation;
        validateBusinessRules();
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
                maturityDate, 15, BigDecimal.valueOf(0.02), false, TransactionLimit.ofFixedTermAccount());
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
                maturityDate, 15, BigDecimal.valueOf(0.05), false, TransactionLimit.ofFixedTermAccount());
    }

    public static FixedTermAccount reconstruct(String id, String customerId, String customerType,
                                      String documentType, String documentNumber, String accountNumber, String externalAccountNumber, AccountType accountType, AccountStatus status, Balance balance, Audit audit, Set<AccountHolder> holders, LocalDate maturityDate, int dayOfOperation, BigDecimal interestRate, boolean hasPerformedMonthlyOperation, TransactionLimit transactionLimit) {
        return new FixedTermAccount(id, customerId, customerType, documentType, documentNumber, accountNumber, externalAccountNumber, accountType, status, balance, audit, holders, maturityDate, dayOfOperation, interestRate, hasPerformedMonthlyOperation, transactionLimit);
    }

    @Override
    protected void validateBusinessRules() {
        if(isEnterpriseAccount()) throw new IllegalArgumentException("Enterprise accounts are not allowed");
        if (this.balance.getAmount().compareTo(BigDecimal.valueOf(1000)) < 0) throw new IllegalArgumentException("Fixed term account requires minimum initial balance of 1000");
        if (interestRate.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Interest rate must be positive");
        if(LocalDate.now().getDayOfMonth() < dayOfOperation || LocalDate.now().getDayOfMonth() > dayOfOperation) throw new IllegalArgumentException("Fixed term accounts can only be created on the operation date");
    }

    @Override
    protected void canPerformTransactionSpecific(OperationType operation, BigDecimal amount) {
        if(LocalDate.now().getDayOfMonth() != this.dayOfOperation) throw new IllegalArgumentException("Fixed term accounts cannot perform operations on operation date");
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
        return null;
    }

    @Override
    public Account withNewBalance(Balance newBalance) {
        return null;
    }

    @Override
    public Account withNewTransactionLimit(TransactionLimit newTransactionLimit) {
        return null;
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
                    this.transactionLimit
            );
        }
        return this;
    }

    private LocalDate calculateOperationDateForMonth(LocalDate date) {
        int dayOfMonth = date.getDayOfMonth();
        int midMonth = date.lengthOfMonth() / 2;

        if (dayOfMonth <= midMonth) return date.withDayOfMonth(midMonth);

        return date.withDayOfMonth(date.lengthOfMonth());

    }

}
