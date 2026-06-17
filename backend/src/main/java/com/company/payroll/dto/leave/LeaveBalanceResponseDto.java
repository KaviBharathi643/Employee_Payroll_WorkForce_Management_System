package com.company.payroll.dto.leave;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LeaveBalanceResponseDto {

    private BigDecimal paidLeaveUsed;
    private BigDecimal remainingPaidLeave;
    private BigDecimal unpaidLeaveUsed;
    private Integer annualPaidLeaveLimit;
}
