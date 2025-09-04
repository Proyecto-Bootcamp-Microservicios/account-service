package com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity;

import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.BalanceCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "accounts")
@Getter
@Setter
@TypeAlias("SAVING")
public class SavingAccountCollection extends AccountCollection {

    public SavingAccountCollection(String id, String customerId, String customerType, String accountType, String accountNumber, String externalAccountNumber, String status, BalanceCollection balance, List<AccountHolderCollection> holders, LocalDateTime createdAt, LocalDateTime updatedAt, TransactionLimitCollection transactionLimit) {
        super(id, customerId, customerType, accountType, accountNumber, externalAccountNumber, status, balance, holders, createdAt, updatedAt, transactionLimit);
    }
}
