package com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class MonthlyMovementLimitCollection {
    private int limit;
    private LocalDate monthStartDate;
    private int currentMovements;
}
