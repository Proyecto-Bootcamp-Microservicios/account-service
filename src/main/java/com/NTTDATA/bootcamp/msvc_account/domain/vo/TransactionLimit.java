package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import com.NTTDATA.bootcamp.msvc_account.domain.enums.OperationType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author Mario Espinoza P.
 * @version 1.0
 * @since 2025-08-30
 * */
@Getter
public final class TransactionLimit {
    private final Map<OperationType, Integer> maxFreeTransactions;
    private final Map<OperationType, BigDecimal> fixedCommissions;
    private final Map<OperationType, BigDecimal> percentageCommissions;
    private final Map<OperationType, Integer> currentTransactions;
    private final LocalDate monthStartDate;

    public static final BigDecimal MAX_COMMISSION = new BigDecimal("500.00");
    public static final BigDecimal MIN_COMMISSION = new BigDecimal("1.00");
    public static final int MAX_FREE_TRANSACTIONS = 10;
    public static final int UNLIMITED = Integer.MAX_VALUE;

    private static final Map<OperationType, Integer> MAP_OPERATION_INT_ZERO;
    static {
        MAP_OPERATION_INT_ZERO = new EnumMap<>(OperationType.class);
        MAP_OPERATION_INT_ZERO.put(OperationType.DEPOSIT, 0);
        MAP_OPERATION_INT_ZERO.put(OperationType.WITHDRAWAL, 0);
    }

    private static final Map<OperationType, BigDecimal> MAP_OPERATION_BIGDECIMAL_ZERO;
    static {
        MAP_OPERATION_BIGDECIMAL_ZERO = new EnumMap<>(OperationType.class);
        MAP_OPERATION_BIGDECIMAL_ZERO.put(OperationType.DEPOSIT, BigDecimal.ZERO);
        MAP_OPERATION_BIGDECIMAL_ZERO.put(OperationType.WITHDRAWAL, BigDecimal.ZERO);
    }

    private static final Map<OperationType, Integer> MAP_OPERATION_INT_UNLIMITED;
    static {
        MAP_OPERATION_INT_UNLIMITED = new EnumMap<>(OperationType.class);
        MAP_OPERATION_INT_UNLIMITED.put(OperationType.DEPOSIT, UNLIMITED);
        MAP_OPERATION_INT_UNLIMITED.put(OperationType.WITHDRAWAL, UNLIMITED);
    }

    private TransactionLimit(Map<OperationType, Integer> maxFreeTransactions,
                             Map<OperationType, BigDecimal> fixedCommissions,
                             Map<OperationType, BigDecimal> percentageCommissions,
                             Map<OperationType, Integer> currentTransactions,
                             LocalDate monthStartDate) {
        if(maxFreeTransactions == null || fixedCommissions == null || percentageCommissions == null
                || currentTransactions == null || monthStartDate == null) throw new IllegalArgumentException("TransactionLimit cannot be null");

        maxFreeTransactions.forEach((operationType, maxFree) -> {
            if(operationType.equals(OperationType.TRANSFER)) throw new IllegalArgumentException("The transfer cannot have a maximum of free transactions, they are free");
            if(maxFree < 0) throw new IllegalArgumentException("Max free transactions cannot be negative");
            if(maxFree != UNLIMITED && maxFree > MAX_FREE_TRANSACTIONS) throw new IllegalArgumentException("Max free transactions cannot be greater than " + MAX_FREE_TRANSACTIONS);
        });

        percentageCommissions.forEach((operationType, commission) -> {
            if(operationType.equals(OperationType.TRANSFER)) throw new IllegalArgumentException("The transfer cannot have a maximum of free transactions, they are free");
            if(commission.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Commission per transaction cannot be negative");
            if(commission.compareTo(BigDecimal.valueOf(100)) > 0) throw new IllegalArgumentException("Commission per transaction cannot be greater than 100%");
        });

        fixedCommissions.forEach((operationType, commission) -> {
            if(operationType.equals(OperationType.TRANSFER)) throw new IllegalArgumentException("The transfer cannot have a maximum of free transactions, they are free");
            if(commission.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Commission per transaction cannot be negative");
            if(commission.compareTo(MAX_COMMISSION) > 0) throw new IllegalArgumentException("Commission per transaction cannot be greater than " + MAX_COMMISSION);
        });

        currentTransactions.forEach((operationType, current) -> {
            if(operationType.equals(OperationType.TRANSFER)) throw new IllegalArgumentException("The transfer cannot have a maximum of free transactions, they are free");
            if(current < 0) throw new IllegalArgumentException("Current transactions cannot be negative");
        });
        this.maxFreeTransactions = new EnumMap<>(maxFreeTransactions);
        this.fixedCommissions = new EnumMap<>(fixedCommissions);
        this.percentageCommissions = new EnumMap<>(percentageCommissions);
        this.currentTransactions = new EnumMap<>(currentTransactions);
        this.monthStartDate = monthStartDate;
    }

    /**
     * Default method factory
     * */
    public static TransactionLimit of(){
        Map<OperationType, Integer> maxFreeTransactions = Map.of(OperationType.DEPOSIT, 2, OperationType.WITHDRAWAL, 2);
        Map<OperationType, BigDecimal> percentageCommissions = Map.of(OperationType.DEPOSIT, new BigDecimal("1.00"), OperationType.WITHDRAWAL, new BigDecimal("1.00"));
        return new TransactionLimit(maxFreeTransactions, MAP_OPERATION_BIGDECIMAL_ZERO, percentageCommissions, MAP_OPERATION_INT_ZERO, LocalDate.now().withDayOfMonth(1));
    }

    /**
     * Limit for Fixed Term Account*/
    public static TransactionLimit ofFixedTermAccount(){
        Map<OperationType, Integer> maxFreeTransactions = Map.of(OperationType.DEPOSIT, 1, OperationType.WITHDRAWAL, 1);
        Map<OperationType, BigDecimal> percentageCommissions = Map.of(OperationType.DEPOSIT, new BigDecimal("1.00"), OperationType.WITHDRAWAL, new BigDecimal("1.00"));
        return new TransactionLimit(maxFreeTransactions, MAP_OPERATION_BIGDECIMAL_ZERO, percentageCommissions, MAP_OPERATION_INT_ZERO, LocalDate.now().withDayOfMonth(1));
    }

    public static TransactionLimit reconstruct(Map<OperationType, Integer> maxFreeTransactions, Map<OperationType, BigDecimal> fixedCommissions, Map<OperationType, BigDecimal> percentageCommissions, Map<OperationType, Integer> currentTransactions, LocalDate monthStartDate){
        return new TransactionLimit(maxFreeTransactions, fixedCommissions, percentageCommissions, currentTransactions, monthStartDate);
    }

    public TransactionLimit resetForNewMonth() {
        if (isNewMonth()) return new TransactionLimit(maxFreeTransactions, fixedCommissions, percentageCommissions, MAP_OPERATION_INT_ZERO, currentMonthStart());
        return this;
    }

    public int getFreeTransactionsPerType(OperationType operationType) {
        return maxFreeTransactions.getOrDefault(operationType, 0);
    }

    public int getCurrentTransactionsPerType(OperationType operationType) {
        return currentTransactions.getOrDefault(operationType, 0);
    }

    public BigDecimal getFixedCommissionPerType(OperationType operationType) {
        return fixedCommissions.getOrDefault(operationType, BigDecimal.ZERO);
    }

    public BigDecimal getPercentageCommissionPerType(OperationType operationType) {
        return percentageCommissions.getOrDefault(operationType, BigDecimal.ZERO);
    }

    private LocalDate currentMonthStart() {
        return LocalDate.now().withDayOfMonth(1);
    }

    private boolean isNewMonth() {
        return !monthStartDate.equals(currentMonthStart());
    }
}
