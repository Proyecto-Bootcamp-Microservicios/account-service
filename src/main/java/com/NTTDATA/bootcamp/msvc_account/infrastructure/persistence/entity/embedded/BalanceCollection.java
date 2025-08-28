package com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BalanceCollection {
    private BigDecimal amount;
    private String currencyCode;
    private LocalDateTime lastUpdate;
}
