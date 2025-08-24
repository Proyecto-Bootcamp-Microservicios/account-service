package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public final class MonthlyMovementLimit {
    private final int limit;
    private final LocalDate monthStartDate;
    private final int currentMovements;

    private MonthlyMovementLimit(int limit, LocalDate monthStartDate, int currentMovements) {
        if (limit < 0) throw new IllegalArgumentException("Limit cannot be negative");
        if (monthStartDate == null) throw new IllegalArgumentException("Month start date cannot be null");
        if (currentMovements < 0) throw new IllegalArgumentException("Current movements cannot be negative");
        this.limit = limit;
        this.monthStartDate = monthStartDate;
        this.currentMovements = currentMovements;
    }

    public static MonthlyMovementLimit of(int limit) {
        return new MonthlyMovementLimit(limit, LocalDate.now().withDayOfMonth(1), 0);
    }

    public boolean hasReachedLimit() {
        return this.currentMovements >= this.limit;
    }

    public boolean canPerformMovement() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        return currentMonth.equals(monthStartDate) && currentMovements < limit;
    }

    public boolean isNewMonth() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        return !currentMonth.equals(monthStartDate);
    }

    public int remainingMovements() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        if (!currentMonth.equals(monthStartDate)) {
            return limit; // Nuevo mes, reset completo
        }
        return Math.max(0, limit - currentMovements);
    }

    public boolean isLimitReached() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        return currentMonth.equals(monthStartDate) && currentMovements >= limit;
    }

    public boolean isUnlimited() {
        return limit == Integer.MAX_VALUE;
    }

}
