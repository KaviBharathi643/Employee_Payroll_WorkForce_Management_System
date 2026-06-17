package com.company.payroll.dto.payroll;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GeneratePayrollRequestDto {

    @NotNull(message = "Payroll year is required")
    @Min(value = 2000, message = "Invalid payroll year")
    private Integer payrollYear;

    @NotNull(message = "Payroll month is required")
    @Min(value = 1, message = "Payroll month must be between 1 and 12")
    @Max(value = 12, message = "Payroll month must be between 1 and 12")
    private Integer payrollMonth;

    private BigDecimal bonusAmount;
}
