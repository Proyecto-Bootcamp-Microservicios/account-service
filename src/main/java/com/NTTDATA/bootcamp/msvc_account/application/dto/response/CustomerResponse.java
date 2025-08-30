package com.NTTDATA.bootcamp.msvc_account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public final class CustomerResponse {
    private final String id;
    private final String name;
    private final String customerType;
    private final String documentNumber;
    private final String email;
    private final boolean active;
    private final LocalDateTime createdAt;
}
