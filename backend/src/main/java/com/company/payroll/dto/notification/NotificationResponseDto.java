package com.company.payroll.dto.notification;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDto {

    private Long notificationId;
    private String notificationType;
    private String title;
    private String message;
    private String status;
    private String relatedEntityType;
    private Long relatedEntityId;
    private LocalDateTime createdAt;
}
