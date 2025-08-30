package com.NTTDATA.bootcamp.msvc_account.infrastructure.persistence.entity.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthorizedSignerCollection {
    private String documentType;
    private String documentNumber;
}
