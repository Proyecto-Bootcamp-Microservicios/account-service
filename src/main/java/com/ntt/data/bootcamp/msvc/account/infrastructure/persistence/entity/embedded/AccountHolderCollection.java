package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class AccountHolderCollection {
  private String documentType;
  private String documentNumber;
  private BigDecimal participationPercentage;
  private boolean isPrimary;
}
