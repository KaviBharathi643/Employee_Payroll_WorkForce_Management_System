package com.company.payroll.controller;

import com.company.payroll.dto.leave.ApplyLeaveRequestDto;
import com.company.payroll.dto.leave.LeaveBalanceResponseDto;
import com.company.payroll.dto.leave.LeaveResponseDto;
import com.company.payroll.service.LeaveService;
import com.company.payroll.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR')")
    public ApiResponse<LeaveResponseDto> applyLeave(@Valid @RequestBody ApplyLeaveRequestDto request) {
        LeaveResponseDto response = leaveService.applyLeave(request);
        return ApiResponse.success("Leave Applied Successfully", response);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ApiResponse<LeaveResponseDto> approveLeave(@PathVariable Long id) {
        LeaveResponseDto response = leaveService.approveLeave(id);
        return ApiResponse.success("Leave Approved Successfully", response);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ApiResponse<LeaveResponseDto> rejectLeave(@PathVariable Long id) {
        LeaveResponseDto response = leaveService.rejectLeave(id);
        return ApiResponse.success("Leave Rejected Successfully", response);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<LeaveResponseDto> cancelLeave(@PathVariable Long id) {
        LeaveResponseDto response = leaveService.cancelLeave(id);
        return ApiResponse.success("Leave Cancelled Successfully", response);
    }

    @PutMapping("/{id}/convert-unpaid")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ApiResponse<LeaveResponseDto> convertToUnpaid(@PathVariable Long id) {
        LeaveResponseDto response = leaveService.convertToUnpaidLeave(id);
        return ApiResponse.success("Leave Converted To Unpaid Successfully", response);
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<List<LeaveResponseDto>> getMyLeaveHistory() {
        List<LeaveResponseDto> response = leaveService.getMyLeaveHistory();
        return ApiResponse.success("Leave history retrieved successfully", response);
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<LeaveBalanceResponseDto> getLeaveBalance() {
        LeaveBalanceResponseDto response = leaveService.getLeaveBalance();
        return ApiResponse.success("Leave balance retrieved successfully", response);
    }

    @GetMapping("/employee/{id}")
    @PreAuthorize("hasRole('HR')")
    public ApiResponse<List<LeaveResponseDto>> getEmployeeLeaveHistory(@PathVariable Long id) {
        List<LeaveResponseDto> response = leaveService.getEmployeeLeaveHistory(id);
        return ApiResponse.success("Employee leave history retrieved successfully", response);
    }

    @GetMapping("/hr/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<LeaveResponseDto>> getHrLeaveHistory(@PathVariable Long id) {
        List<LeaveResponseDto> response = leaveService.getHrLeaveHistory(id);
        return ApiResponse.success("HR leave history retrieved successfully", response);
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ApiResponse<List<LeaveResponseDto>> getLeaveReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Long userId) {
        List<LeaveResponseDto> response = leaveService.getLeaveReportData(fromDate, toDate, userId);
        return ApiResponse.success("Leave Report Generated Successfully", response);
    }
}
