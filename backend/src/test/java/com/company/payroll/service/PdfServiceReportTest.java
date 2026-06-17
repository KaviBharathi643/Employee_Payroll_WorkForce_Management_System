package com.company.payroll.service;

import com.company.payroll.constants.ReportConstants;
import com.company.payroll.dto.report.ReportPdfData;
import com.company.payroll.entity.SystemSettings;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfServiceReportTest {

    private final PdfService pdfService = new PdfService();

    @Test
    void createReportPdf_withNoRecords_returnsValidPdf() {
        SystemSettings settings = SystemSettings.builder()
                .companyName("Acme Corp")
                .companyAddress("123 Main St")
                .companyEmail("hr@acme.com")
                .build();

        ReportPdfData reportData = ReportPdfData.builder()
                .reportType(ReportConstants.EMPLOYEE_REPORT)
                .generatedBy("Test User")
                .headers(List.of("Code", "Name"))
                .rows(List.of())
                .summary(new LinkedHashMap<>(Map.of("Total Users", "0")))
                .build();

        byte[] pdf = pdfService.createReportPdf(settings, reportData);

        assertNotNull(pdf);
        assertTrue(pdf.length > 100);
        assertTrue(pdf[0] == '%' && pdf[1] == 'P' && pdf[2] == 'D' && pdf[3] == 'F');
    }
}
