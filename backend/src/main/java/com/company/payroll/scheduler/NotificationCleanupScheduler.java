package com.company.payroll.scheduler;

import com.company.payroll.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCleanupScheduler {

    private final NotificationService notificationService;

    @Scheduled(cron = "0 30 2 * * *")
    public void runNotificationCleanup() {
        try {
            notificationService.cleanupExpiredNotifications();
        } catch (Exception ex) {
            log.error("Notification cleanup scheduler failed", ex);
        }
    }
}
