package com.NTTDATA.bootcamp.msvc_account.domain;

import com.NTTDATA.bootcamp.msvc_account.domain.vo.MonthlyMovementLimit;
import lombok.Getter;

@Getter
public class SavingsAccount extends Account {
    private final MonthlyMovementLimit monthlyMovementLimit;
}
