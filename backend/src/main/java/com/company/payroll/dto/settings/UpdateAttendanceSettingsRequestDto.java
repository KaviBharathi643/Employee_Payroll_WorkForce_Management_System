package com.company.payroll.dto.settings;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateAttendanceSettingsRequestDto {

    @NotNull(message = "Office start time is required")
    private LocalTime officeStartTime;

    @NotNull(message = "Office end time is required")
    private LocalTime officeEndTime;

    @NotNull(message = "Checkout reminder hours is required")
    @Min(value = 0, message = "Checkout reminder hours must be greater than or equal to 0")
    private Integer checkoutReminderHours;
}
