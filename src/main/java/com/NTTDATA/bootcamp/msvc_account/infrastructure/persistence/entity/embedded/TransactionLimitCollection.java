package com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded;

import com.NTTDATA.bootcamp.msvc_account.domain.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class TransactionLimitCollection {
    private Map<OperationType, Integer> maxFreeTransactions;
    private Map<OperationType, BigDecimal> fixedCommissions;
    private Map<OperationType, BigDecimal> percentageCommissions;
    private Map<OperationType, Integer> currentTransactions;
    private LocalDate monthStartDate;
}
