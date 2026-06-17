package com.company.payroll.util;

import com.company.payroll.constants.LeaveConstants;
import com.company.payroll.exception.ValidationException;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

public final class LeaveCalculationUtil {

    private LeaveCalculationUtil() {
    }

    public static BigDecimal calculateLeaveDays(LocalDate startDate, LocalDate endDate, String durationType) {
        return switch (durationType) {
            case LeaveConstants.HALF_DAY -> BigDecimal.valueOf(0.5);
            case LeaveConstants.FULL_DAY -> BigDecimal.ONE;
            case LeaveConstants.MULTI_DAY -> countWeekdays(startDate, endDate);
            default -> throw new ValidationException("Invalid Duration Type");
        };
    }

    public static void validateDateRange(LocalDate startDate, LocalDate endDate, String durationType) {
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date cannot be before start date");
        }
        if (LeaveConstants.HALF_DAY.equals(durationType) || LeaveConstants.FULL_DAY.equals(durationType)) {
            if (!startDate.equals(endDate)) {
                throw new ValidationException("Start and end date must be the same for single-day leave");
            }
        }
    }

    private static BigDecimal countWeekdays(LocalDate startDate, LocalDate endDate) {
        int days = 0;
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            DayOfWeek day = current.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                days++;
            }
            current = current.plusDays(1);
        }
        if (days == 0) {
            throw new ValidationException("Leave must include at least one working day");
        }
        return BigDecimal.valueOf(days);
    }
}
