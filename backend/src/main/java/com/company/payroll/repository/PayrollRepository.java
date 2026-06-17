package com.company.payroll.repository;

import com.company.payroll.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    Optional<Payroll> findByUserIdAndPayrollYearAndPayrollMonth(Long userId, Integer payrollYear, Integer payrollMonth);

    List<Payroll> findByUserIdOrderByPayrollYearDescPayrollMonthDesc(Long userId);

    @Query("""
            SELECT p FROM Payroll p
            JOIN p.user u
            WHERE u.role = :role
            ORDER BY p.payrollYear DESC, p.payrollMonth DESC
            """)
    List<Payroll> findByUserRole(@Param("role") String role);

    @Query("""
            SELECT p FROM Payroll p
            JOIN p.user u
            WHERE u.role = :role
              AND p.payrollYear = :year
              AND p.payrollMonth = :month
            ORDER BY p.employeeName ASC
            """)
    List<Payroll> findByUserRoleAndPeriod(
            @Param("role") String role,
            @Param("year") Integer year,
            @Param("month") Integer month);

    @Query("""
            SELECT COUNT(p) FROM Payroll p
            JOIN p.user u
            WHERE u.role = :role
              AND p.payrollYear = :year
              AND p.payrollMonth = :month
            """)
    long countByUserRoleAndPeriod(
            @Param("role") String role,
            @Param("year") Integer year,
            @Param("month") Integer month);
}
