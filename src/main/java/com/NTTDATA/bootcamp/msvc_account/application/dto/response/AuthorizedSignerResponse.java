package com.NTTDATA.bootcamp.msvc_account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class AuthorizedSignerResponse {
    private final String documentType;
    private final String documentNumber;
}
