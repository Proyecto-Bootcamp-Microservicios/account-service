package com.NTTDATA.bootcamp.msvc_account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class AccountHolderResponse {
    private final String documentType;
    private final String documentNumber;
}
