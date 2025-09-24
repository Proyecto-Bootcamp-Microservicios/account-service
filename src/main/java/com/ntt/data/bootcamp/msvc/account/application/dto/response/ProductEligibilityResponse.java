package com.ntt.data.bootcamp.msvc.account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductEligibilityResponse {
  private String customerId;

  private Boolean isEligible;

  private String reason;

  private List<OverdueProduct> overdueProducts;

  private OffsetDateTime checkedAt;
}
