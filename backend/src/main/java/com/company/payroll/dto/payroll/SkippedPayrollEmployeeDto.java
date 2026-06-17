package com.company.payroll.dto.payroll;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkippedPayrollEmployeeDto {

    private String employeeCode;
    private String employeeName;
    private String reason;
}
