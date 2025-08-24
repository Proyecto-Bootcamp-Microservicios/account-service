package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import lombok.Getter;

import java.util.List;

import static com.NTTDATA.bootcamp.msvc_account.domain.util.DomainUtils.*;

@Getter
public final class AuthorizedSigner {
    private final String customerId;
    private final String documentNumber;
    private final String documentType;

    private AuthorizedSigner(String customerId, String documentNumber, String documentType) {
        reformat(List.of(customerId, documentNumber, documentType));
        if (customerId == null || customerId.isEmpty()) throw new IllegalArgumentException("Customer ID cannot be null or empty");
        if (!isValidUUID(customerId)) throw new IllegalArgumentException("Customer ID must be a valid UUID");
        if (documentNumber == null || documentNumber.isEmpty()) throw new IllegalArgumentException("Document number cannot be null or empty");
        if (documentType == null || documentType.isEmpty()) throw new IllegalArgumentException("Document type cannot be null or empty");
        this.customerId = customerId;
        this.documentNumber = documentNumber;
        this.documentType = documentType;
    }

    public static AuthorizedSigner of(String customerId, String documentNumber, String documentType) {
        return new AuthorizedSigner(customerId, documentNumber, documentType);
    }

}
