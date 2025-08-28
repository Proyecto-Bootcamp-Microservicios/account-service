package com.NTTDATA.bootcamp.msvc_account.domain.vo;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public final class MonthlyMovementLimit {
    private final int limit;
    private final LocalDate monthStartDate;
    private final int currentMovements;

    private final static int MAX_MONTHLY_LIMIT = 100;
    private final static int UNLIMITED = Integer.MAX_VALUE;

    private MonthlyMovementLimit(int limit, LocalDate monthStartDate, int currentMovements) {
        if (limit < 0) throw new IllegalArgumentException("Limit cannot be negative");
        if (limit != UNLIMITED && limit > MAX_MONTHLY_LIMIT) throw new IllegalArgumentException("The limit cannot be exceeded " + MAX_MONTHLY_LIMIT + " movements");
        if (monthStartDate == null) throw new IllegalArgumentException("Month start date cannot be null");
        if (currentMovements < 0) throw new IllegalArgumentException("Current movements cannot be negative");
        if (currentMovements > limit) throw new IllegalArgumentException("Current movements cannot be greater than limit");
        this.limit = limit;
        this.monthStartDate = monthStartDate;
        this.currentMovements = currentMovements;
    }

    public static MonthlyMovementLimit of(int limit) {
        return new MonthlyMovementLimit(limit, LocalDate.now().withDayOfMonth(1), 0);
    }

    public static MonthlyMovementLimit unlimited() {
        return new MonthlyMovementLimit(UNLIMITED, LocalDate.now().withDayOfMonth(1), 0);
    }

    public static MonthlyMovementLimit reconstruct(int limit, LocalDate monthStartDate, int currentMovements) {
        return new MonthlyMovementLimit(limit, monthStartDate, currentMovements);
    }

    public MonthlyMovementLimit incrementMovements() {
        if (isUnlimited()) return this;
        return new MonthlyMovementLimit(limit, monthStartDate, currentMovements + 1);
    }

    public MonthlyMovementLimit syncWithRealCount(int realMovementCount) {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

        if (realMovementCount > this.limit) {
            return new MonthlyMovementLimit(
                    this.limit,
                    currentMonth,
                    0
            );
        }

        if (!currentMonth.equals(this.monthStartDate)) {
            return new MonthlyMovementLimit(
                    this.limit,
                    currentMonth,
                    0
            );
        }

        // Caso 3: Mismo mes, conteo válido, sincronizar
        return new MonthlyMovementLimit(
                this.limit,
                this.monthStartDate,
                realMovementCount
        );
    }

    public boolean canPerformMovement() {
        if (isUnlimited()) return true;
        LocalDate currentMonth = currentMonthStart();
        return currentMonth.equals(monthStartDate) && currentMovements < limit;
    }

    public boolean isNewMonth() {
        LocalDate currentMonth = currentMonthStart();
        return !currentMonth.equals(monthStartDate);
    }

    public MonthlyMovementLimit resetForNewMonth() {
        if (!isNewMonth()) return this;
        return new MonthlyMovementLimit(limit, currentMonthStart(), 0);
    }


    public int remainingMovements() {
        LocalDate currentMonth = currentMonthStart();
        if (!currentMonth.equals(monthStartDate)) return limit;
        return Math.max(0, limit - currentMovements);
    }

    public boolean isLimitReached() {
        if (isUnlimited()) return false;
        LocalDate currentMonth = currentMonthStart();
        return currentMonth.equals(monthStartDate) && currentMovements >= limit;
    }

    public boolean isUnlimited() {
        return limit == UNLIMITED;
    }

    private LocalDate currentMonthStart() {
        return LocalDate.now().withDayOfMonth(1);
    }

}
