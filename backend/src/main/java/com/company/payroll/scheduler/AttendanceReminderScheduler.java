package com.company.payroll.scheduler;

import com.company.payroll.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceReminderScheduler {

    private final AttendanceService attendanceService;

    @Scheduled(cron = "0 10 * * * *")
    public void runMissingCheckoutReminder() {
        try {
            attendanceService.processMissingCheckoutReminders();
        } catch (Exception ex) {
            log.error("Missing checkout scheduler failed", ex);
        }
    }
}
