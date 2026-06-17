package com.company.payroll.dto.settings;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Builder
public class SystemSettingsResponseDto {

    private String companyName;
    private String companyAddress;
    private String companyEmail;
    private String companyPhone;
    private LocalTime officeStartTime;
    private LocalTime officeEndTime;
    private Integer checkoutReminderHours;
    private Integer annualPaidLeaveLimit;
    private BigDecimal pfPercentage;
    private Integer salaryCreditDay;
}
