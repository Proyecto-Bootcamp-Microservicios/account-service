package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class AccountHolderCollection {
  private String documentType;
  private String documentNumber;
  private BigDecimal participationPercentage;
  private boolean isPrimary;
}
