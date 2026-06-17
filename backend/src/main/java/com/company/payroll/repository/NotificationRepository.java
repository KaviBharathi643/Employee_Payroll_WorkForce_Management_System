package com.company.payroll.repository;

import com.company.payroll.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END
            FROM Notification n
            WHERE n.user.id = :userId
              AND n.title = :title
              AND n.createdAt >= :since
            """)
    boolean existsByUserIdAndTitleSince(
            @Param("userId") Long userId,
            @Param("title") String title,
            @Param("since") LocalDateTime since);

    @Query("""
            SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END
            FROM Notification n
            WHERE n.user.id = :userId
              AND n.redirectType = :redirectType
            """)
    boolean existsByUserIdAndRedirectType(
            @Param("userId") Long userId,
            @Param("redirectType") String redirectType);

    List<Notification> findTop10ByUser_IdAndExpiresAtAfterOrderByCreatedAtDesc(
            Long userId,
            LocalDateTime now);

    List<Notification> findByUser_IdAndCreatedAtAfterOrderByCreatedAtDesc(
            Long userId,
            LocalDateTime since);

    long countByUser_IdAndIsReadFalseAndExpiresAtAfter(Long userId, LocalDateTime now);

    Optional<Notification> findByIdAndUser_Id(Long id, Long userId);

    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.isRead = true
            WHERE n.user.id = :userId
              AND n.isRead = false
            """)
    int markAllAsReadByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.expiresAt < :now")
    int deleteByExpiresAtBefore(@Param("now") LocalDateTime now);
}
