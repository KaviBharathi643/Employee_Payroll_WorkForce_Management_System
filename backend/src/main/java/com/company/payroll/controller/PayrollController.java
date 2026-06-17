package com.company.payroll.controller;

import com.company.payroll.dto.payroll.GeneratePayrollRequestDto;
import com.company.payroll.dto.payroll.GeneratePayrollResponseDto;
import com.company.payroll.dto.payroll.PayrollResponseDto;
import com.company.payroll.dto.payroll.PayrollSummaryResponseDto;
import com.company.payroll.service.PayrollService;
import com.company.payroll.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ApiResponse<GeneratePayrollResponseDto> generatePayroll(
            @Valid @RequestBody GeneratePayrollRequestDto request) {
        GeneratePayrollResponseDto response = payrollService.generatePayroll(request);
        return ApiResponse.success("Payroll Generated Successfully", response);
    }

    @PostMapping("/{id}/credit")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ApiResponse<PayrollResponseDto> creditSalary(@PathVariable Long id) {
        PayrollResponseDto response = payrollService.creditSalary(id);
        return ApiResponse.success("Salary Credited Successfully", response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR','ADMIN','EMPLOYEE')")
    public ApiResponse<List<PayrollResponseDto>> listPayrolls() {
        List<PayrollResponseDto> response = payrollService.listPayrolls();
        return ApiResponse.success("Payrolls retrieved successfully", response);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ApiResponse<PayrollSummaryResponseDto> getPayrollSummary() {
        PayrollSummaryResponseDto response = payrollService.getPayrollSummary();
        return ApiResponse.success("Payroll summary retrieved successfully", response);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<List<PayrollResponseDto>> getPayrollHistory() {
        List<PayrollResponseDto> response = payrollService.getPayrollHistory();
        return ApiResponse.success("Payroll history retrieved successfully", response);
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ApiResponse<List<PayrollResponseDto>> getPayrollReport(
            @RequestParam Integer payrollYear,
            @RequestParam Integer payrollMonth) {
        List<PayrollResponseDto> response = payrollService.getPayrollReport(payrollYear, payrollMonth);
        return ApiResponse.success("Payroll Report Generated Successfully", response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR','ADMIN','EMPLOYEE')")
    public ApiResponse<PayrollResponseDto> getPayrollDetails(@PathVariable Long id) {
        PayrollResponseDto response = payrollService.getPayrollDetails(id);
        return ApiResponse.success("Payroll retrieved successfully", response);
    }
}
