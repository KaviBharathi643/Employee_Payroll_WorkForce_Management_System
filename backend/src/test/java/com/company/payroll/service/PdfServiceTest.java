package com.company.payroll.service;

import com.company.payroll.entity.Payroll;
import com.company.payroll.entity.Payslip;
import com.company.payroll.entity.SystemSettings;
import com.company.payroll.entity.UserBankDetails;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfServiceTest {

    private final PdfService pdfService = new PdfService();

    @Test
    void createPayslipPdf_returnsNonEmptyPdfBytes() {
        SystemSettings settings = SystemSettings.builder()
                .companyName("Acme Corp")
                .companyAddress("123 Main St")
                .companyEmail("hr@acme.com")
                .build();

        Payroll payroll = Payroll.builder()
                .employeeCode("EMP001")
                .employeeName("Jane Doe")
                .department("Engineering")
                .designation("Developer")
                .payrollMonth(5)
                .payrollYear(2026)
                .basicSalary(BigDecimal.valueOf(50000))
                .bonus(BigDecimal.valueOf(2000))
                .pfPercentage(BigDecimal.valueOf(8))
                .pfAmount(BigDecimal.valueOf(4000))
                .unpaidLeaveCount(BigDecimal.ZERO)
                .unpaidLeaveDeduction(BigDecimal.ZERO)
                .finalSalary(BigDecimal.valueOf(48000))
                .build();

        Payslip payslip = Payslip.builder()
                .payslipNumber("PSL-2026-0001")
                .generatedDate(LocalDateTime.of(2026, 6, 1, 10, 0))
                .build();

        UserBankDetails bank = UserBankDetails.builder()
                .bankName("Test Bank")
                .accountNumber("1234567890")
                .ifscCode("TEST0001234")
                .build();

        byte[] pdf = pdfService.createPayslipPdf(settings, payroll, payslip, bank);

        assertNotNull(pdf);
        assertTrue(pdf.length > 100);
        assertTrue(pdf[0] == '%' && pdf[1] == 'P' && pdf[2] == 'D' && pdf[3] == 'F');
    }
}
