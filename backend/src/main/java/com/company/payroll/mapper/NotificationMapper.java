package com.company.payroll.mapper;

import com.company.payroll.constants.NotificationConstants;
import com.company.payroll.dto.notification.NotificationListResponseDto;
import com.company.payroll.dto.notification.NotificationResponseDto;
import com.company.payroll.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponseDto toResponse(Notification notification) {
        RedirectParts parts = parseRedirectType(notification.getRedirectType());
        return NotificationResponseDto.builder()
                .notificationId(notification.getId())
                .notificationType(parts.entityType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .status(toStatus(notification.getIsRead()))
                .relatedEntityType(parts.entityType())
                .relatedEntityId(parts.entityId())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public NotificationListResponseDto toListItem(Notification notification) {
        RedirectParts parts = parseRedirectType(notification.getRedirectType());
        return NotificationListResponseDto.builder()
                .notificationId(notification.getId())
                .notificationType(parts.entityType())
                .title(notification.getTitle())
                .status(toStatus(notification.getIsRead()))
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private String toStatus(Boolean isRead) {
        return Boolean.TRUE.equals(isRead)
                ? NotificationConstants.STATUS_READ
                : NotificationConstants.STATUS_UNREAD;
    }

    RedirectParts parseRedirectType(String redirectType) {
        if (redirectType == null || redirectType.isBlank()) {
            return new RedirectParts(null, null);
        }
        int separator = redirectType.indexOf(':');
        if (separator < 0) {
            return new RedirectParts(redirectType, null);
        }
        String entityType = redirectType.substring(0, separator);
        Long entityId = null;
        String idPart = redirectType.substring(separator + 1);
        if (!idPart.isBlank()) {
            entityId = Long.parseLong(idPart);
        }
        return new RedirectParts(entityType, entityId);
    }

    record RedirectParts(String entityType, Long entityId) {
    }
}
