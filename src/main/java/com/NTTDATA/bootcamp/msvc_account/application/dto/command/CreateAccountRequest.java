package com.NTTDATA.bootcamp.msvc_account.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/*to json example: {
    "customerId": "1",
    "customerType": "1",
    "documentType": "1",
    "documentNumber": "1"
}*/
@Getter
@AllArgsConstructor
public final class CreateAccountRequest {
    private final String customerId;
    private final String customerType;
    private final String documentType;
    private final String documentNumber;
    private final BigDecimal amount;
}
