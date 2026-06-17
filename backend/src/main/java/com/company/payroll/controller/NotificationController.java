package com.company.payroll.controller;

import com.company.payroll.dto.notification.NotificationBellResponseDto;
import com.company.payroll.dto.notification.NotificationListResponseDto;
import com.company.payroll.dto.notification.NotificationResponseDto;
import com.company.payroll.service.NotificationService;
import com.company.payroll.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<List<NotificationListResponseDto>> getLatestNotifications() {
        List<NotificationListResponseDto> response = notificationService.getLatestNotifications();
        return ApiResponse.success("Notification Retrieved Successfully", response);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<List<NotificationListResponseDto>> getNotificationHistory() {
        List<NotificationListResponseDto> response = notificationService.getNotificationHistory();
        return ApiResponse.success("Notification Retrieved Successfully", response);
    }

    @GetMapping("/bell")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<NotificationBellResponseDto> getNotificationBellData() {
        NotificationBellResponseDto response = notificationService.getNotificationBellData();
        return ApiResponse.success("Notification Retrieved Successfully", response);
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<Void> markAllNotificationsAsRead() {
        notificationService.markAllNotificationsAsRead();
        return ApiResponse.success("All Notifications Marked As Read", null);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<NotificationResponseDto> markNotificationAsRead(@PathVariable Long id) {
        NotificationResponseDto response = notificationService.markNotificationAsRead(id);
        return ApiResponse.success("Notification Marked As Read", response);
    }
}
