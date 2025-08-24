package com.NTTDATA.bootcamp.msvc_account.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class FixedTermAccount extends Account {
    private final LocalDate maturityDate;
    private final LocalDate operationDate;
    private final BigDecimal interestRate;
    private final boolean hasPerformedMonthlyOperation;

}
