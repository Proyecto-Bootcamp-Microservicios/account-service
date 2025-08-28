package com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity;

import com.NTTDATA.bootcamp.msvc_account.domain.enums.AccountType;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.BalanceCollection;
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
public class AccountCollection {

    @Id
    private String id;
    private String customerType;
    private String customerId;

    private String accountType;
    @Indexed
    private String accountNumber;
    @Indexed
    private String externalAccountNumber;
    private String status;

    private List<AccountHolderCollection> holders;
    private BalanceCollection balance;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
