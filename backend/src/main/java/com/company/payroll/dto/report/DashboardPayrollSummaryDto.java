package com.company.payroll.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardPayrollSummaryDto {

    private BigDecimal totalSalaryPaid;
    private BigDecimal totalPfDeduction;
    private BigDecimal totalUnpaidLeaveDeduction;
    private long generatedPayrollCount;
    private long creditedPayrollCount;
    private long pendingSalaryCredits;
}
