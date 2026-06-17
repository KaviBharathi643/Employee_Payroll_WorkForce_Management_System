package com.company.payroll.dto.notification;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationListResponseDto {

    private Long notificationId;
    private String notificationType;
    private String title;
    private String status;
    private LocalDateTime createdAt;
}
