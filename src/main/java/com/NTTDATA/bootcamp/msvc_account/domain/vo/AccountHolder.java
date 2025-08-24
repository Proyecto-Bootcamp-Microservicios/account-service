package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

import static com.NTTDATA.bootcamp.msvc_account.domain.util.DomainUtils.*;

@Getter
public final class AccountHolder {
    private final String customerId;
    private final String documentNumber;
    private final String documentType;
    private final BigDecimal participationPercentage;
    private final boolean primaryHolder;

    private AccountHolder(String customerId, String documentNumber, String documentType, BigDecimal participationPercentage, boolean primaryHolder) {
        reformat(List.of(customerId, documentNumber, documentType));
        if (customerId == null || customerId.isEmpty()) throw new IllegalArgumentException("Customer ID cannot be null or empty");
        if (!isValidUUID(customerId)) throw new IllegalArgumentException("Customer ID must be a valid UUID");
        if (documentNumber == null || documentNumber.isEmpty()) throw new IllegalArgumentException("Document number cannot be null or empty");
        if (participationPercentage == null ||
                participationPercentage.compareTo(BigDecimal.ZERO) <= 0 ||
                participationPercentage.compareTo(BigDecimal.valueOf(100)) > 0) throw new IllegalArgumentException("Participation percentage must be between 0 and 100");

        this.customerId = customerId;
        this.documentNumber = documentNumber;
        this.documentType = documentType;
        this.participationPercentage = participationPercentage;
        this.primaryHolder = primaryHolder;
    }

    public static AccountHolder ofPrimaryHolder(String customerId, String documentNumber, String documentType) {
        return new AccountHolder(customerId, documentNumber, documentType, BigDecimal.valueOf(100), true);
    }

    public static AccountHolder ofSecondaryHolder(String customerId, String documentNumber, String documentType, BigDecimal participationPercentage) {
        return new AccountHolder(customerId, documentNumber, documentType, participationPercentage, false);
    }

}
