package com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity;

import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.AccountHolderCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.AuthorizedSignerCollection;
import com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded.BalanceCollection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "accounts")
@Getter
@Setter
@TypeAlias("FIXED_TERM")
public class FixedTermAccountCollection extends AccountCollection {

    private LocalDate maturityDate;
    private LocalDate operationDate;
    private BigDecimal interestRate;
    private boolean hasPerformedMonthlyOperation;

    public FixedTermAccountCollection(String id, String customerId, String customerType,
                                     String accountType, String accountNumber, String externalAccountNumber,
                                     String status, List<AccountHolderCollection> holders,
                                     BalanceCollection balance, LocalDateTime createdAt, LocalDateTime updatedAt,
                                     LocalDate maturityDate, LocalDate operationDate, BigDecimal interestRate, boolean hasPerformedMonthlyOperation) {
        super(id, customerId, customerType, accountType, accountNumber, externalAccountNumber, status, balance, holders, createdAt, updatedAt);
        this.maturityDate = maturityDate;
        this.operationDate = operationDate;
        this.interestRate = interestRate;
        this.hasPerformedMonthlyOperation = hasPerformedMonthlyOperation;
    }

}
