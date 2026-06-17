package com.company.payroll.dto.settings;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePayrollSettingsRequestDto {

    @NotNull(message = "PF percentage is required")
    @DecimalMin(value = "0.0", message = "PF percentage must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "PF percentage must be between 0 and 100")
    private BigDecimal pfPercentage;

    @NotNull(message = "Salary credit day is required")
    @Min(value = 1, message = "Salary credit day must be between 1 and 31")
    @Max(value = 31, message = "Salary credit day must be between 1 and 31")
    private Integer salaryCreditDay;
}
