package com.company.payroll.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSettings extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_address", nullable = false, columnDefinition = "TEXT")
    private String companyAddress;

    @Column(name = "company_email", nullable = false)
    private String companyEmail;

    @Column(name = "company_phone", nullable = false, length = 20)
    private String companyPhone;

    @Column(name = "office_start_time", nullable = false)
    private LocalTime officeStartTime;

    @Column(name = "office_end_time", nullable = false)
    private LocalTime officeEndTime;

    @Column(name = "checkout_reminder_time", nullable = false)
    private LocalTime checkoutReminderTime;

    @Column(name = "annual_paid_leave_limit", nullable = false)
    private Integer annualPaidLeaveLimit;

    @Column(name = "pf_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal pfPercentage;

    @Column(name = "salary_credit_day", nullable = false)
    private Integer salaryCreditDay;
}
