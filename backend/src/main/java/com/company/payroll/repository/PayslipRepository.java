package com.company.payroll.repository;

import com.company.payroll.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    Optional<Payslip> findByPayrollId(Long payrollId);

    Optional<Payslip> findTopByPayslipNumberStartingWithOrderByPayslipNumberDesc(String prefix);

    @Query("""
            SELECT p FROM Payslip p
            JOIN FETCH p.payroll pr
            JOIN pr.user u
            WHERE u.id = :userId
            ORDER BY p.generatedDate DESC
            """)
    List<Payslip> findByPayrollUserIdOrderByGeneratedDateDesc(@Param("userId") Long userId);

    @Query("""
            SELECT p FROM Payslip p
            JOIN FETCH p.payroll pr
            JOIN pr.user u
            WHERE u.role = :role
            ORDER BY p.generatedDate DESC
            """)
    List<Payslip> findByPayrollUserRoleOrderByGeneratedDateDesc(@Param("role") String role);

    @Query("""
            SELECT p FROM Payslip p
            JOIN FETCH p.payroll pr
            JOIN pr.user u
            WHERE p.id = :id
            """)
    Optional<Payslip> findByIdWithPayroll(@Param("id") Long id);
}
