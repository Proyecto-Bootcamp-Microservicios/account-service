package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import lombok.Getter;

import java.util.List;

import static com.NTTDATA.bootcamp.msvc_account.domain.util.DomainUtils.*;

@Getter
public final class AuthorizedSigner {
    private final String documentNumber;
    private final String documentType;

    private AuthorizedSigner(String documentNumber, String documentType) {
        reformat(List.of(documentNumber, documentType));
        if (documentNumber == null || documentNumber.isEmpty()) throw new IllegalArgumentException("Document number cannot be null or empty");
        if (documentType == null || documentType.isEmpty()) throw new IllegalArgumentException("Document type cannot be null or empty");
        this.documentNumber = documentNumber;
        this.documentType = documentType;
    }

    public static AuthorizedSigner of(String documentNumber, String documentType) {
        return new AuthorizedSigner(documentNumber, documentType);
    }

}
