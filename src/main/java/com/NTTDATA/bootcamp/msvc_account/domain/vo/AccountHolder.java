package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

import static com.NTTDATA.bootcamp.msvc_account.domain.util.DomainUtils.*;

@Getter
public final class AccountHolder {
    private final String documentNumber;
    private final String documentType;
    private final BigDecimal participationPercentage;
    private final boolean primaryHolder;

    private AccountHolder(String documentType, String documentNumber, BigDecimal participationPercentage, boolean primaryHolder) {
        reformat(List.of(documentNumber, documentType));
        if (documentNumber == null || documentNumber.isEmpty()) throw new IllegalArgumentException("Document number cannot be null or empty");
        if (participationPercentage == null ||
                participationPercentage.compareTo(BigDecimal.ZERO) <= 0 ||
                participationPercentage.compareTo(BigDecimal.valueOf(100)) > 0) throw new IllegalArgumentException("Participation percentage must be between 0 and 100");

        this.documentNumber = documentNumber;
        this.documentType = documentType;
        this.participationPercentage = participationPercentage;
        this.primaryHolder = primaryHolder;
    }

    public static AccountHolder ofPrimaryHolder(String documentType, String documentNumber) {
        return new AccountHolder(documentType, documentNumber, BigDecimal.valueOf(100), true);
    }

    public static AccountHolder ofPrimaryHolder(String documentType, String documentNumber, BigDecimal recalculatedPercentage) {
        return new AccountHolder(documentType, documentNumber, recalculatedPercentage, true);
    }

    public static AccountHolder ofSecondaryHolder(String documentType, String documentNumber, BigDecimal participationPercentage) {
        return new AccountHolder(documentType, documentNumber, participationPercentage, false);
    }

}
