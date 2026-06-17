package com.company.payroll.controller;

import com.company.payroll.dto.report.DashboardAnalyticsResponseDto;
import com.company.payroll.service.DashboardAnalyticsService;
import com.company.payroll.service.ReportService;
import com.company.payroll.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final DashboardAnalyticsService dashboardAnalyticsService;

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<byte[]> generateEmployeeReport(
            @RequestParam(required = false) String employmentStatus,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String employeeCode) {
        byte[] pdf = reportService.generateEmployeeReport(employmentStatus, department, designation, employeeCode);
        return pdfResponse("employee-report", pdf);
    }

    @GetMapping("/attendance")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<byte[]> generateAttendanceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String employeeCode) {
        byte[] pdf = reportService.generateAttendanceReport(fromDate, toDate, department, designation, employeeCode);
        return pdfResponse("attendance-report", pdf);
    }

    @GetMapping("/leaves")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<byte[]> generateLeaveReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String employeeCode,
            @RequestParam(required = false) String leaveType,
            @RequestParam(required = false) String leaveStatus) {
        byte[] pdf = reportService.generateLeaveReport(
                fromDate, toDate, department, designation, employeeCode, leaveType, leaveStatus);
        return pdfResponse("leave-report", pdf);
    }

    @GetMapping("/payroll")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<byte[]> generatePayrollReport(
            @RequestParam Integer payrollYear,
            @RequestParam Integer payrollMonth) {
        byte[] pdf = reportService.generatePayrollReport(payrollYear, payrollMonth);
        return pdfResponse("payroll-report", pdf);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ApiResponse<DashboardAnalyticsResponseDto> getDashboardAnalytics() {
        DashboardAnalyticsResponseDto response = dashboardAnalyticsService.getDashboardAnalytics();
        return ApiResponse.success("Dashboard Analytics Retrieved Successfully", response);
    }

    private ResponseEntity<byte[]> pdfResponse(String prefix, byte[] pdf) {
        String filename = reportService.buildReportFileName(prefix);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
