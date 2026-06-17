package com.company.payroll.controller;

import com.company.payroll.dto.payslip.PayslipListResponseDto;
import com.company.payroll.dto.payslip.PayslipResponseDto;
import com.company.payroll.service.PayslipService;
import com.company.payroll.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
@RequiredArgsConstructor
public class PayslipController {

    private final PayslipService payslipService;

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<List<PayslipListResponseDto>> getPayslipList() {
        List<PayslipListResponseDto> response = payslipService.getPayslipList();
        return ApiResponse.success("Payslip Retrieved Successfully", response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<PayslipResponseDto> getPayslipDetails(@PathVariable Long id) {
        PayslipResponseDto response = payslipService.getPayslipDetails(id);
        return ApiResponse.success("Payslip Retrieved Successfully", response);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long id) {
        PayslipService.PayslipDownloadResult download = payslipService.downloadPayslip(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.filename() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(download.content());
    }
}
