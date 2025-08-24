package com.NTTDATA.bootcamp.msvc_account.domain;

import com.NTTDATA.bootcamp.msvc_account.domain.vo.AuthorizedSigner;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
public final class CheckingAccount extends Account {
    private final BigDecimal maintenanceFee;
    private final LocalDate nextFeeDate;
    private final Set<AuthorizedSigner> signers;


}
