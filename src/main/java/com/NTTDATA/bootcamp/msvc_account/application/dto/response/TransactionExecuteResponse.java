package com.NTTDATA.bootcamp.msvc_account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@AllArgsConstructor
public final class TransactionExecuteResponse {
    private final String accountId;
    private final String accountType;
    private final BigDecimal newBalance;
    private final BigDecimal previousBalance;
    private final Map<String, Integer> currentTransactions;
    private final BigDecimal commission;
    private final String status; // SUCCESS, FAILED
    private final String displayAmount;
    private final String message;
    private final String transactionId;
    private final String executedAt;
    private final TransactionLimitInfo transactionLimitInfo;

    @Getter
    @AllArgsConstructor
    public static final class TransactionLimitInfo {
        private final Map<String, Integer> maxFreeTransactions;
        private final Map<String, Integer> remainingFreeTransactions;
        private final boolean isFreeTransaction;
    }
}
