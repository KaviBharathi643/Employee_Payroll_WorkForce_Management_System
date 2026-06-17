package com.company.payroll.dto.leave;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LeaveResponseDto {

    private Long leaveId;
    private Long userId;
    private String employeeCode;
    private String employeeName;
    private String leaveType;
    private String durationType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal leaveDays;
    private String status;
    private String reason;
    private Long approvedById;
    private String approvedByName;
}
