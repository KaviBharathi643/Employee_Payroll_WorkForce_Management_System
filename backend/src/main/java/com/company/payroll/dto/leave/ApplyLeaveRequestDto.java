package com.company.payroll.dto.leave;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ApplyLeaveRequestDto {

    @NotBlank(message = "Leave type is required")
    @Pattern(regexp = "CASUAL|SICK|UNPAID", message = "Invalid leave type")
    private String leaveType;

    @NotBlank(message = "Duration type is required")
    @Pattern(regexp = "HALF_DAY|FULL_DAY|MULTI_DAY", message = "Invalid duration type")
    private String durationType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Leave reason is required")
    private String reason;
}
