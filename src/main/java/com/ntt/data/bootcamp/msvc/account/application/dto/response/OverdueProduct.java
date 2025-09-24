package com.ntt.data.bootcamp.msvc.account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OverdueProduct {
  private String productId;

  private String productNumber;

  /**
   * Type of overdue product
   */
  public enum ProductTypeEnum {
    CREDIT("CREDIT"),

    CREDIT_CARD("CREDIT_CARD");

    private String value;

    ProductTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

  }

  private ProductTypeEnum productType;

  private Integer overdueDays;

  private Double overdueAmount;
}
