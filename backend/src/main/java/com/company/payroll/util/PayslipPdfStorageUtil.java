package com.company.payroll.util;

import com.company.payroll.exception.BusinessRuleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PayslipPdfStorageUtil {

    @Value("${app.upload.payslip-dir:uploads/payslips}")
    private String payslipUploadDir;

    public Path resolvePdfPath(String payslipNumber) {
        return getPayslipUploadPath().resolve(payslipNumber + ".pdf");
    }

    public void savePdf(String payslipNumber, byte[] content) {
        Path target = resolvePdfPath(payslipNumber);
        try {
            Files.write(target, content);
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to save payslip PDF");
        }
    }

    public byte[] readPdf(String payslipNumber) {
        Path path = resolvePdfPath(payslipNumber);
        if (!Files.exists(path)) {
            return null;
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to read payslip PDF");
        }
    }

    public boolean pdfExists(String payslipNumber) {
        return Files.exists(resolvePdfPath(payslipNumber));
    }

    private Path getPayslipUploadPath() {
        Path path = Paths.get(payslipUploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to initialize payslip upload directory");
        }
        return path;
    }
}
