package com.company.payroll.repository;

import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
            SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
            FROM LeaveRequest l
            WHERE l.user.id = :userId
              AND l.status = 'APPROVED'
              AND l.startDate <= :endDate
              AND l.endDate >= :startDate
            """)
    boolean existsApprovedLeaveBetweenDates(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
            FROM LeaveRequest l
            WHERE l.user.id = :userId
              AND l.status = 'APPROVED'
              AND :date BETWEEN l.startDate AND l.endDate
            """)
    boolean existsApprovedLeaveOnDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("""
            SELECT COALESCE(SUM(l.leaveDays), 0)
            FROM LeaveRequest l
            WHERE l.user.id = :userId
              AND l.status = 'APPROVED'
              AND l.leaveType IN ('CASUAL', 'SICK')
              AND EXTRACT(YEAR FROM l.startDate) = :year
            """)
    BigDecimal sumApprovedPaidLeaveDays(@Param("userId") Long userId, @Param("year") int year);

    @Query("""
            SELECT COALESCE(SUM(l.leaveDays), 0)
            FROM LeaveRequest l
            WHERE l.user.id = :userId
              AND l.status = 'APPROVED'
              AND l.leaveType = 'UNPAID'
              AND EXTRACT(YEAR FROM l.startDate) = :year
            """)
    BigDecimal sumApprovedUnpaidLeaveDays(@Param("userId") Long userId, @Param("year") int year);

    @Query("""
            SELECT l FROM LeaveRequest l
            WHERE l.user.id = :userId
              AND l.status = 'APPROVED'
              AND :date BETWEEN l.startDate AND l.endDate
            """)
    Optional<LeaveRequest> findApprovedLeaveForDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("""
            SELECT l FROM LeaveRequest l
            JOIN l.user u
            WHERE u.role = :role
              AND (:fromDate IS NULL OR l.endDate >= :fromDate)
              AND (:toDate IS NULL OR l.startDate <= :toDate)
            ORDER BY l.createdAt DESC
            """)
    List<LeaveRequest> findByUserRoleAndDateRange(
            @Param("role") String role,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("""
            SELECT l FROM LeaveRequest l
            WHERE l.user = :user
              AND (:fromDate IS NULL OR l.endDate >= :fromDate)
              AND (:toDate IS NULL OR l.startDate <= :toDate)
            ORDER BY l.createdAt DESC
            """)
    List<LeaveRequest> findByUserAndDateRange(
            @Param("user") User user,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("""
            SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
            FROM LeaveRequest l
            WHERE l.user.id = :userId
              AND l.startDate = :date
              AND l.reason LIKE 'Missing Checkout Penalty%'
            """)
    boolean existsMissingCheckoutPenalty(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("""
            SELECT l FROM LeaveRequest l
            WHERE l.user.id = :userId
              AND l.leaveType = 'UNPAID'
              AND l.status = 'APPROVED'
            """)
    List<LeaveRequest> findApprovedUnpaidLeavesByUserId(@Param("userId") Long userId);
}
