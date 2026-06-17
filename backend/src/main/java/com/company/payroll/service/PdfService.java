package com.company.payroll.service;

import com.company.payroll.constants.ReportConstants;
import com.company.payroll.dto.report.ReportPdfData;
import com.company.payroll.entity.Payroll;
import com.company.payroll.entity.Payslip;
import com.company.payroll.entity.SystemSettings;
import com.company.payroll.entity.UserBankDetails;
import com.company.payroll.util.BankMaskingUtil;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PdfService {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font BODY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 11);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] createPayslipPdf(
            SystemSettings settings,
            Payroll payroll,
            Payslip payslip,
            UserBankDetails bank) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addLine(document, settings.getCompanyName(), TITLE_FONT, Element.ALIGN_CENTER);
            addLine(document, settings.getCompanyAddress(), BODY_FONT, Element.ALIGN_CENTER);
            addLine(document, settings.getCompanyEmail(), BODY_FONT, Element.ALIGN_CENTER);
            document.add(new Paragraph(" "));

            addLine(document, "PAYSLIP", HEADER_FONT, Element.ALIGN_CENTER);
            document.add(new Paragraph(" "));

            addLine(document, "Employee Information", HEADER_FONT, Element.ALIGN_LEFT);
            addLine(document, "Employee Code: " + payroll.getEmployeeCode(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "Employee Name: " + payroll.getEmployeeName(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "Department: " + payroll.getDepartment(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "Designation: " + payroll.getDesignation(), BODY_FONT, Element.ALIGN_LEFT);
            document.add(new Paragraph(" "));

            addLine(document, "Payroll Information", HEADER_FONT, Element.ALIGN_LEFT);
            addLine(document, "Payroll Month: " + payroll.getPayrollMonth(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "Payroll Year: " + payroll.getPayrollYear(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "Basic Salary: " + payroll.getBasicSalary(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "Bonus: " + payroll.getBonus(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "PF Percentage: " + payroll.getPfPercentage(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "PF Amount: " + payroll.getPfAmount(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "Unpaid Leave Count: " + payroll.getUnpaidLeaveCount(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "Unpaid Leave Deduction: " + payroll.getUnpaidLeaveDeduction(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "Final Salary: " + payroll.getFinalSalary(), BODY_FONT, Element.ALIGN_LEFT);
            document.add(new Paragraph(" "));

            if (bank != null) {
                addLine(document, "Bank Information", HEADER_FONT, Element.ALIGN_LEFT);
                addLine(document, "Bank Name: " + bank.getBankName(), BODY_FONT, Element.ALIGN_LEFT);
                addLine(document, "Account Number: " + BankMaskingUtil.maskAccountNumber(bank.getAccountNumber()),
                        BODY_FONT, Element.ALIGN_LEFT);
                addLine(document, "IFSC Code: " + bank.getIfscCode(), BODY_FONT, Element.ALIGN_LEFT);
                document.add(new Paragraph(" "));
            }

            addLine(document, "Document Information", HEADER_FONT, Element.ALIGN_LEFT);
            addLine(document, "Payslip Number: " + payslip.getPayslipNumber(), BODY_FONT, Element.ALIGN_LEFT);
            addLine(document, "Generated Date: " + payslip.getGeneratedDate().format(DATE_FORMAT), BODY_FONT,
                    Element.ALIGN_LEFT);
            addLine(document, "System Generated Document", BODY_FONT, Element.ALIGN_LEFT);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException ex) {
            log.error("Failed to generate payslip PDF", ex);
            throw new com.company.payroll.exception.BusinessRuleException("PDF Generation Failed");
        } catch (Exception ex) {
            log.error("Failed to generate payslip PDF", ex);
            throw new com.company.payroll.exception.BusinessRuleException("PDF Generation Failed");
        }
    }

    public byte[] createReportPdf(SystemSettings settings, ReportPdfData reportData) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addLine(document, settings.getCompanyName(), TITLE_FONT, Element.ALIGN_CENTER);
            addLine(document, settings.getCompanyAddress(), BODY_FONT, Element.ALIGN_CENTER);
            addLine(document, settings.getCompanyEmail(), BODY_FONT, Element.ALIGN_CENTER);
            document.add(new Paragraph(" "));

            addLine(document, reportData.getReportType(), HEADER_FONT, Element.ALIGN_CENTER);
            document.add(new Paragraph(" "));

            addLine(document, "Generated Date: " + LocalDateTime.now().format(DATE_FORMAT), BODY_FONT,
                    Element.ALIGN_LEFT);
            addLine(document, "Generated By: " + reportData.getGeneratedBy(), BODY_FONT, Element.ALIGN_LEFT);
            if (reportData.getDateRange() != null && !reportData.getDateRange().isBlank()) {
                addLine(document, "Date Range: " + reportData.getDateRange(), BODY_FONT, Element.ALIGN_LEFT);
            }
            document.add(new Paragraph(" "));

            List<List<String>> rows = reportData.getRows();
            if (rows == null || rows.isEmpty()) {
                addLine(document, ReportConstants.NO_RECORDS, HEADER_FONT, Element.ALIGN_CENTER);
            } else {
                addTable(document, reportData.getHeaders(), rows);
            }

            if (reportData.getSummary() != null && !reportData.getSummary().isEmpty()) {
                document.add(new Paragraph(" "));
                addLine(document, "Summary", HEADER_FONT, Element.ALIGN_LEFT);
                for (Map.Entry<String, String> entry : reportData.getSummary().entrySet()) {
                    addLine(document, entry.getKey() + ": " + entry.getValue(), BODY_FONT, Element.ALIGN_LEFT);
                }
            }

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException ex) {
            log.error("Failed to generate report PDF", ex);
            throw new com.company.payroll.exception.BusinessRuleException("PDF Generation Failed");
        } catch (Exception ex) {
            log.error("Failed to generate report PDF", ex);
            throw new com.company.payroll.exception.BusinessRuleException("PDF Generation Failed");
        }
    }

    private void addTable(Document document, List<String> headers, List<List<String>> rows) throws DocumentException {
        int columnCount = headers.size();
        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(header, HEADER_FONT));
            table.addCell(cell);
        }

        for (List<String> row : rows) {
            for (int i = 0; i < columnCount; i++) {
                String value = i < row.size() && row.get(i) != null ? row.get(i) : "";
                table.addCell(new PdfPCell(new Paragraph(value, BODY_FONT)));
            }
        }

        document.add(table);
    }

    private void addLine(Document document, String text, Font font, int alignment) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(alignment);
        document.add(paragraph);
    }
}
