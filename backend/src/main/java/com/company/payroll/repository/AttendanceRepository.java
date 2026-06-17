package com.company.payroll.repository;

import com.company.payroll.entity.Attendance;
import com.company.payroll.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserAndAttendanceDate(User user, LocalDate attendanceDate);

    Optional<Attendance> findByUserIdAndAttendanceDate(Long userId, LocalDate attendanceDate);

    List<Attendance> findByUserIdOrderByAttendanceDateDesc(Long userId);

    List<Attendance> findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            Long userId, LocalDate fromDate, LocalDate toDate);

    @Query("""
            SELECT a FROM Attendance a
            WHERE a.attendanceDate = :date
              AND a.checkInTime IS NOT NULL
              AND a.checkOutTime IS NULL
            """)
    List<Attendance> findByAttendanceDateWithoutCheckout(@Param("date") LocalDate date);

    @Query("""
            SELECT COUNT(a) FROM Attendance a
            JOIN a.user u
            WHERE a.attendanceDate = :date
              AND a.checkInTime IS NOT NULL
              AND a.checkOutTime IS NULL
              AND u.role = :role
            """)
    long countMissingCheckoutByRoleAndDate(@Param("role") String role, @Param("date") LocalDate date);

    @Query("""
            SELECT a FROM Attendance a
            JOIN FETCH a.user u
            WHERE u.role = :role
              AND a.attendanceDate BETWEEN :fromDate AND :toDate
            ORDER BY a.attendanceDate DESC
            """)
    List<Attendance> findByUserRoleAndAttendanceDateBetween(
            @Param("role") String role,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);
}
