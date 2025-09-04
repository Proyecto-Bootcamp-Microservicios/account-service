package com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity;

import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.BalanceCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.TransactionLimitCollection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "accounts")
@Getter
@Setter
@AllArgsConstructor
public abstract class AccountCollection {

    @Id
    private String id;
    private String customerId;
    private String customerType;
    private String accountType;
    @Indexed
    private String accountNumber;
    @Indexed
    private String externalAccountNumber;
    private String status;

    private BalanceCollection balance;
    private List<AccountHolderCollection> holders;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private TransactionLimitCollection transactionLimit;

}
