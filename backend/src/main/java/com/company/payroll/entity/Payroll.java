package com.company.payroll.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payrolls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "payroll_year", nullable = false)
    private Integer payrollYear;

    @Column(name = "payroll_month", nullable = false)
    private Integer payrollMonth;

    @Column(name = "employee_code", nullable = false, length = 20)
    private String employeeCode;

    @Column(name = "employee_name", nullable = false, length = 150)
    private String employeeName;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false, length = 100)
    private String designation;

    @Column(name = "basic_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal basicSalary;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal bonus;

    @Column(name = "pf_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal pfPercentage;

    @Column(name = "pf_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal pfAmount;

    @Column(name = "unpaid_leave_count", nullable = false, precision = 4, scale = 1)
    private BigDecimal unpaidLeaveCount;

    @Column(name = "unpaid_leave_deduction", nullable = false, precision = 10, scale = 2)
    private BigDecimal unpaidLeaveDeduction;

    @Column(name = "final_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalSalary;

    @Column(name = "generated_date", nullable = false)
    private LocalDateTime generatedDate;

    @Column(name = "credited_date")
    private LocalDateTime creditedDate;

    @ManyToOne
    @JoinColumn(name = "generated_by")
    private User generatedBy;

    @ManyToOne
    @JoinColumn(name = "credited_by")
    private User creditedBy;

    @Column(nullable = false, length = 20)
    private String status;
}
