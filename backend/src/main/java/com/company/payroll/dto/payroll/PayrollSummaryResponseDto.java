package com.company.payroll.dto.payroll;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayrollSummaryResponseDto {

    private BigDecimal totalSalaryPaid;
    private long totalEmployeesPaid;
    private BigDecimal totalUnpaidLeaveDeductions;
    private long pendingSalaryCredits;
}
