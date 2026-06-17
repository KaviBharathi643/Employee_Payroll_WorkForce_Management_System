package com.company.payroll.service;

import com.company.payroll.constants.LeaveConstants;
import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.SystemSettings;
import com.company.payroll.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PayrollCalculationService {

    private static final BigDecimal DAYS_IN_MONTH = BigDecimal.valueOf(30);
    private static final int MONEY_SCALE = 2;

    private final LeaveRequestRepository leaveRequestRepository;

    public BigDecimal calculatePfAmount(BigDecimal basicSalary, BigDecimal pfPercentage) {
        if (pfPercentage == null || pfPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
        return basicSalary
                .multiply(pfPercentage)
                .divide(BigDecimal.valueOf(100), MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateUnpaidLeaveCount(Long userId, int payrollYear, int payrollMonth) {
        BigDecimal total = BigDecimal.ZERO;
        for (LeaveRequest leave : leaveRequestRepository.findApprovedUnpaidLeavesByUserId(userId)) {
            total = total.add(countUnpaidDaysInMonth(leave, payrollYear, payrollMonth));
        }
        return total.setScale(1, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateUnpaidLeaveDeduction(BigDecimal basicSalary, BigDecimal unpaidLeaveCount) {
        if (unpaidLeaveCount == null || unpaidLeaveCount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
        BigDecimal dailySalary = basicSalary.divide(DAYS_IN_MONTH, MONEY_SCALE, RoundingMode.HALF_UP);
        return dailySalary.multiply(unpaidLeaveCount).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateFinalSalary(
            BigDecimal basicSalary,
            BigDecimal bonus,
            BigDecimal pfAmount,
            BigDecimal unpaidLeaveDeduction) {
        BigDecimal safeBonus = bonus != null ? bonus : BigDecimal.ZERO;
        return basicSalary
                .add(safeBonus)
                .subtract(pfAmount)
                .subtract(unpaidLeaveDeduction)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal resolvePfPercentage(SystemSettings settings) {
        return settings.getPfPercentage() != null ? settings.getPfPercentage() : BigDecimal.ZERO;
    }

    private BigDecimal countUnpaidDaysInMonth(LeaveRequest leave, int payrollYear, int payrollMonth) {
        if (LeaveConstants.HALF_DAY.equals(leave.getDurationType())) {
            LocalDate date = leave.getStartDate();
            if (isInPayrollMonth(date, payrollYear, payrollMonth) && !isWeekend(date)) {
                return BigDecimal.valueOf(0.5);
            }
            return BigDecimal.ZERO;
        }

        BigDecimal days = BigDecimal.ZERO;
        LocalDate current = leave.getStartDate();
        LocalDate end = leave.getEndDate();
        while (!current.isAfter(end)) {
            if (isInPayrollMonth(current, payrollYear, payrollMonth) && !isWeekend(current)) {
                days = days.add(BigDecimal.ONE);
            }
            current = current.plusDays(1);
        }
        return days;
    }

    private boolean isInPayrollMonth(LocalDate date, int payrollYear, int payrollMonth) {
        return date.getYear() == payrollYear && date.getMonthValue() == payrollMonth;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
