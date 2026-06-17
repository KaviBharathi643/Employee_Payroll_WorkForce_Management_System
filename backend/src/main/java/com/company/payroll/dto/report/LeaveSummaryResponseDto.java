package com.company.payroll.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LeaveSummaryResponseDto {

    private long pendingLeaveCount;
    private long approvedLeaveCount;
    private long rejectedLeaveCount;
    private long cancelledLeaveCount;
    private BigDecimal paidLeaveUsed;
    private BigDecimal remainingPaidLeave;
    private BigDecimal unpaidLeaveUsed;
}
