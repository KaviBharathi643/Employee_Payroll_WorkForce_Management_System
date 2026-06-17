package com.company.payroll.mapper;

import com.company.payroll.constants.NotificationConstants;
import com.company.payroll.entity.Notification;
import com.company.payroll.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NotificationMapperTest {

    private final NotificationMapper notificationMapper = new NotificationMapper();

    @Test
    void toResponse_parsesRedirectTypeWithEntityId() {
        Notification notification = Notification.builder()
                .id(1L)
                .user(User.builder().id(10L).build())
                .title("Leave Approved")
                .message("Your leave request has been approved.")
                .redirectType("LEAVE:42")
                .isRead(false)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .build();

        var response = notificationMapper.toResponse(notification);

        assertEquals("LEAVE", response.getNotificationType());
        assertEquals("LEAVE", response.getRelatedEntityType());
        assertEquals(42L, response.getRelatedEntityId());
        assertEquals(NotificationConstants.STATUS_UNREAD, response.getStatus());
    }

    @Test
    void toResponse_parsesRedirectTypeWithoutEntityId() {
        Notification notification = Notification.builder()
                .id(2L)
                .user(User.builder().id(10L).build())
                .title("Payroll Reminder")
                .message("Payroll pending.")
                .redirectType("PAYROLL")
                .isRead(true)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .build();

        var response = notificationMapper.toResponse(notification);

        assertEquals("PAYROLL", response.getNotificationType());
        assertEquals("PAYROLL", response.getRelatedEntityType());
        assertNull(response.getRelatedEntityId());
        assertEquals(NotificationConstants.STATUS_READ, response.getStatus());
    }
}
