package com.company.payroll.scheduler;

import com.company.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayrollReminderScheduler {

    private final PayrollService payrollService;

    @Scheduled(cron = "0 0 9 * * *")
    public void runPayrollReminder() {
        try {
            payrollService.sendPayrollReminderIfNeeded();
        } catch (Exception ex) {
            log.error("Payroll reminder scheduler failed", ex);
        }
    }
}
