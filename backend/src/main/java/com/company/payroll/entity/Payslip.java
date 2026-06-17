package com.company.payroll.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payslips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payslip extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "payroll_id", nullable = false)
    private Payroll payroll;

    @Column(name = "payslip_number", nullable = false, unique = true, length = 50)
    private String payslipNumber;

    @Column(name = "pdf_generated", nullable = false)
    private Boolean pdfGenerated;

    @Column(name = "generated_date", nullable = false)
    private LocalDateTime generatedDate;

    @Column(nullable = false, length = 20)
    private String status;
}
