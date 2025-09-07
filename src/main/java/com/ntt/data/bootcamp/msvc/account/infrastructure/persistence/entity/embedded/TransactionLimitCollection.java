package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded;

import com.ntt.data.bootcamp.msvc.account.domain.enums.OperationType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
