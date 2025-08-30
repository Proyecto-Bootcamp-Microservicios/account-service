package com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class AccountHolderCollection {
    private String documentType;
    private String documentNumber;
    private BigDecimal participationPercentage;
    private boolean isPrimary;
}
