package com.company.payroll.dto.report;

import com.company.payroll.dto.attendance.AttendanceSummaryResponseDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardAnalyticsResponseDto {

    private UserCountSummaryDto employeeSummary;
    private UserCountSummaryDto hrSummary;
    private AttendanceSummaryResponseDto attendanceSummary;
    private LeaveSummaryResponseDto leaveSummary;
    private DashboardPayrollSummaryDto payrollSummary;
}
