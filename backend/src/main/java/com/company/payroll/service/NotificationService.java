package com.company.payroll.service;

import com.company.payroll.constants.NotificationConstants;
import com.company.payroll.dto.notification.NotificationBellResponseDto;
import com.company.payroll.dto.notification.NotificationListResponseDto;
import com.company.payroll.dto.notification.NotificationResponseDto;
import com.company.payroll.entity.Notification;
import com.company.payroll.entity.User;
import com.company.payroll.exception.ResourceNotFoundException;
import com.company.payroll.mapper.NotificationMapper;
import com.company.payroll.repository.NotificationRepository;
import com.company.payroll.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<NotificationListResponseDto> getLatestNotifications() {
        User current = currentUserService.getCurrentUser();
        return notificationRepository
                .findTop10ByUser_IdAndExpiresAtAfterOrderByCreatedAtDesc(current.getId(), LocalDateTime.now())
                .stream()
                .map(notificationMapper::toListItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationListResponseDto> getNotificationHistory() {
        User current = currentUserService.getCurrentUser();
        LocalDateTime since = LocalDateTime.now().minusDays(NotificationConstants.RETENTION_DAYS);
        return notificationRepository
                .findByUser_IdAndCreatedAtAfterOrderByCreatedAtDesc(current.getId(), since)
                .stream()
                .map(notificationMapper::toListItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationBellResponseDto getNotificationBellData() {
        User current = currentUserService.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        List<NotificationListResponseDto> latest = notificationRepository
                .findTop10ByUser_IdAndExpiresAtAfterOrderByCreatedAtDesc(current.getId(), now)
                .stream()
                .map(notificationMapper::toListItem)
                .toList();
        long unreadCount = notificationRepository.countByUser_IdAndIsReadFalseAndExpiresAtAfter(
                current.getId(), now);
        return NotificationBellResponseDto.builder()
                .latestNotifications(latest)
                .unreadCount(unreadCount)
                .build();
    }

    @Transactional
    public NotificationResponseDto markNotificationAsRead(Long notificationId) {
        Notification notification = getOwnedNotification(notificationId);
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public void markAllNotificationsAsRead() {
        User current = currentUserService.getCurrentUser();
        notificationRepository.markAllAsReadByUserId(current.getId());
    }

    @Transactional
    public int cleanupExpiredNotifications() {
        int deleted = notificationRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        log.info("Notification cleanup completed. Deleted {} expired notifications.", deleted);
        return deleted;
    }

    private Notification getOwnedNotification(Long notificationId) {
        User current = currentUserService.getCurrentUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification Not Found"));
        if (!notification.getUser().getId().equals(current.getId())) {
            throw new AccessDeniedException("Unauthorized Access");
        }
        return notification;
    }
}
