package com.company.payroll.dto.payslip;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PayslipResponseDto {

    private Long payslipId;
    private String payslipNumber;
    private Long payrollId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String designation;
    private Integer payrollMonth;
    private Integer payrollYear;
    private BigDecimal basicSalary;
    private BigDecimal bonus;
    private BigDecimal pfPercentage;
    private BigDecimal pfAmount;
    private BigDecimal unpaidLeaveCount;
    private BigDecimal unpaidLeaveDeduction;
    private BigDecimal finalSalary;
    private String bankName;
    private String maskedAccountNumber;
    private String ifscCode;
    private LocalDateTime generatedDate;
    private Boolean pdfGenerated;
}
