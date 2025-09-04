package com.NTTDATA.bootcamp.msvc_account.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/*to json example: {
    "customerId": "1",
    "amount": 1
}*/
@Getter
@AllArgsConstructor
public final class CreateAccountCommand {
    private final String customerId;
    private final BigDecimal amount;
}
