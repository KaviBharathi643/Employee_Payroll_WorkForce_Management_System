package com.company.payroll.dto.settings;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateLeaveSettingsRequestDto {

    @NotNull(message = "Annual paid leave limit is required")
    @Min(value = 1, message = "Annual paid leave limit must be greater than 0")
    private Integer annualPaidLeaveLimit;
}
