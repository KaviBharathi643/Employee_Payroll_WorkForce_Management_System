package com.company.payroll.mapper;

import com.company.payroll.dto.settings.SystemSettingsResponseDto;
import com.company.payroll.entity.SystemSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Component
public class SystemSettingsMapper {

    public SystemSettingsResponseDto toResponse(SystemSettings settings) {
        return SystemSettingsResponseDto.builder()
                .companyName(settings.getCompanyName())
                .companyAddress(settings.getCompanyAddress())
                .companyEmail(settings.getCompanyEmail())
                .companyPhone(settings.getCompanyPhone())
                .officeStartTime(settings.getOfficeStartTime())
                .officeEndTime(settings.getOfficeEndTime())
                .checkoutReminderHours(resolveCheckoutReminderHours(settings))
                .annualPaidLeaveLimit(settings.getAnnualPaidLeaveLimit())
                .pfPercentage(settings.getPfPercentage())
                .salaryCreditDay(settings.getSalaryCreditDay())
                .build();
    }

    public int resolveCheckoutReminderHours(SystemSettings settings) {
        LocalTime officeEnd = settings.getOfficeEndTime();
        LocalTime reminderTime = settings.getCheckoutReminderTime();
        if (officeEnd == null || reminderTime == null) {
            return 0;
        }
        long minutes = Duration.between(officeEnd, reminderTime).toMinutes();
        if (minutes < 0) {
            minutes += 24 * 60;
        }
        return (int) (minutes / 60);
    }

    public LocalTime computeCheckoutReminderTime(LocalTime officeEndTime, int checkoutReminderHours) {
        return officeEndTime.plusHours(checkoutReminderHours);
    }
}
