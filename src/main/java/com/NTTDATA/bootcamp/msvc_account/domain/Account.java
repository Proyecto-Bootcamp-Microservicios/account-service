package com.NTTDATA.bootcamp.msvc_account.domain;

import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountStatus;
import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountType;
import com.NTTDATA.bootcamp.msvc_account.domain.vo.*;
import lombok.Getter;

import java.util.Set;

@Getter
public abstract class Account {
    protected final AccountId id;
    protected final String customerId;
    protected final String customerType;
    protected final InternalAccountNumber accountNumber;
    protected final ExternalAccountNumber externalAccountNumber;
    protected final AccountType accountType;
    protected final AccountStatus status;
    protected final Balance balance;
    protected final Audit audit;
    protected final Set<AccountHolder> holders;

    private Account(String id, String customerId, String customerType, String documentType, String documentNumber, String accountNumber, String externalAccountNumber, AccountType accountType, AccountStatus status, Balance balance, Audit audit, Set<AccountHolder> holders) {
        this.id = AccountId.of(id);
        this.customerId = customerId;
        this.customerType = customerType;
        this.accountNumber = InternalAccountNumber.of(accountNumber);
        this.externalAccountNumber = ExternalAccountNumber.of(externalAccountNumber);
        this.accountType = accountType;
        this.status = status;
        this.balance = balance;
        this.audit = audit;
        this.holders = Set.copyOf(holders);

        //TODO: CORREGIR PORQUE NO SE AGREGA POR COLECCION INMODIFICABLE
        this.holders.add(AccountHolder.ofPrimaryHolder(customerId, documentType, documentNumber));
    }

    public void suspend() {}

    public void activate() {}

    public void close() {}

    public void addHolder(AccountHolder accountHolder) {}

    public void removeHolder(AccountHolder accountHolder) {}

    public boolean isPersonalAccount() {
        return "PERSONAL".equals(this.customerType);
    }

    public boolean isEnterpriseAccount() {
        return "ENTERPRISE".equals(this.customerType);
    }

}
