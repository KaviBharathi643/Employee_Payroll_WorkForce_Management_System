package com.company.payroll.repository;

import com.company.payroll.entity.OtpVerification;
import com.company.payroll.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByUserOrderByCreatedAtDesc(User user);

    long countByUserAndCreatedAtAfter(User user, LocalDateTime createdAt);

    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
