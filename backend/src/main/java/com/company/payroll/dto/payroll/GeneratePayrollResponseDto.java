package com.company.payroll.dto.payroll;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GeneratePayrollResponseDto {

    private int processedCount;
    private int skippedCount;
    private List<SkippedPayrollEmployeeDto> skippedEmployees;
}
