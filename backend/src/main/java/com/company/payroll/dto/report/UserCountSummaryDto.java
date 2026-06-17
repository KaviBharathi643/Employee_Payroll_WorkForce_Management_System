package com.company.payroll.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCountSummaryDto {

    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
}
