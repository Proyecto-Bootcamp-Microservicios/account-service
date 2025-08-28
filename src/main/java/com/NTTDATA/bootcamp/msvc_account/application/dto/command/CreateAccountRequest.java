package com.NTTDATA.bootcamp.msvc_account.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CreateAccountRequest {
    private final String customerId;
    private final String customerType;
    private final String documentType;
    private final String documentNumber;
    private final String accountType;
}
