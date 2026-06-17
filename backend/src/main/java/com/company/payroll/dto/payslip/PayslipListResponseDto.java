package com.company.payroll.dto.payslip;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PayslipListResponseDto {

    private Long payslipId;
    private String payslipNumber;
    private Integer payrollMonth;
    private Integer payrollYear;
    private LocalDateTime generatedDate;
    private String employeeCode;
    private String employeeName;
}
