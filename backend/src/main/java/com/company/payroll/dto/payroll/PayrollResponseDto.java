package com.company.payroll.dto.payroll;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PayrollResponseDto {

    private Long payrollId;
    private Long userId;
    private Integer payrollYear;
    private Integer payrollMonth;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String designation;
    private BigDecimal basicSalary;
    private BigDecimal bonus;
    private BigDecimal pfPercentage;
    private BigDecimal pfAmount;
    private BigDecimal unpaidLeaveCount;
    private BigDecimal unpaidLeaveDeduction;
    private BigDecimal finalSalary;
    private String status;
    private LocalDateTime generatedDate;
    private LocalDateTime creditedDate;
}
