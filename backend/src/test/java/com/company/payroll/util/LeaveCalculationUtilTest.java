package com.company.payroll.util;

import com.company.payroll.constants.LeaveConstants;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaveCalculationUtilTest {

    @Test
    void calculateHalfDay() {
        BigDecimal days = LeaveCalculationUtil.calculateLeaveDays(
                LocalDate.of(2026, 6, 4),
                LocalDate.of(2026, 6, 4),
                LeaveConstants.HALF_DAY);
        assertEquals(0, BigDecimal.valueOf(0.5).compareTo(days));
    }

    @Test
    void calculateMultiDaySkipsWeekends() {
        LocalDate start = LocalDate.of(2026, 6, 5);
        LocalDate end = LocalDate.of(2026, 6, 8);
        BigDecimal days = LeaveCalculationUtil.calculateLeaveDays(start, end, LeaveConstants.MULTI_DAY);
        assertEquals(0, BigDecimal.valueOf(2).compareTo(days));
    }
}
