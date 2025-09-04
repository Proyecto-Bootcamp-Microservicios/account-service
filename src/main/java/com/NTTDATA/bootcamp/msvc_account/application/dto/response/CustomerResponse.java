package com.NTTDATA.bootcamp.msvc_account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public final class CustomerResponse {
    private String id;
    private String name;
    private String customerType;
    private String documentType;
    private String documentNumber;
    private String email;
    private boolean active;
    private LocalDateTime createdAt;
}
