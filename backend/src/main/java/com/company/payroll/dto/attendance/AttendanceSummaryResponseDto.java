package com.company.payroll.dto.attendance;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceSummaryResponseDto {

    private long presentCount;
    private long absentCount;
    private long leaveCount;
    private long halfDayLeaveCount;
    private long missingCheckoutCount;
}
