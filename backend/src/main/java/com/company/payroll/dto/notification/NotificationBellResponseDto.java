package com.company.payroll.dto.notification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NotificationBellResponseDto {

    private List<NotificationListResponseDto> latestNotifications;
    private long unreadCount;
}
