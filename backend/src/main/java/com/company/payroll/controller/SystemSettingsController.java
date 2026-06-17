package com.company.payroll.controller;

import com.company.payroll.dto.settings.SystemSettingsResponseDto;
import com.company.payroll.dto.settings.UpdateAttendanceSettingsRequestDto;
import com.company.payroll.dto.settings.UpdateCompanySettingsRequestDto;
import com.company.payroll.dto.settings.UpdateLeaveSettingsRequestDto;
import com.company.payroll.dto.settings.UpdatePayrollSettingsRequestDto;
import com.company.payroll.service.SystemSettingsService;
import com.company.payroll.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SystemSettingsController {

    private final SystemSettingsService systemSettingsService;

    @GetMapping
    public ApiResponse<SystemSettingsResponseDto> getSettings() {
        SystemSettingsResponseDto response = systemSettingsService.getSystemSettings();
        return ApiResponse.success("Settings Retrieved Successfully", response);
    }

    @PutMapping("/company")
    public ApiResponse<SystemSettingsResponseDto> updateCompanySettings(
            @Valid @RequestBody UpdateCompanySettingsRequestDto request) {
        SystemSettingsResponseDto response = systemSettingsService.updateCompanySettings(request);
        return ApiResponse.success("Company Settings Updated Successfully", response);
    }

    @PutMapping("/attendance")
    public ApiResponse<SystemSettingsResponseDto> updateAttendanceSettings(
            @Valid @RequestBody UpdateAttendanceSettingsRequestDto request) {
        SystemSettingsResponseDto response = systemSettingsService.updateAttendanceSettings(request);
        return ApiResponse.success("Attendance Settings Updated Successfully", response);
    }

    @PutMapping("/leave")
    public ApiResponse<SystemSettingsResponseDto> updateLeaveSettings(
            @Valid @RequestBody UpdateLeaveSettingsRequestDto request) {
        SystemSettingsResponseDto response = systemSettingsService.updateLeaveSettings(request);
        return ApiResponse.success("Leave Settings Updated Successfully", response);
    }

    @PutMapping("/payroll")
    public ApiResponse<SystemSettingsResponseDto> updatePayrollSettings(
            @Valid @RequestBody UpdatePayrollSettingsRequestDto request) {
        SystemSettingsResponseDto response = systemSettingsService.updatePayrollSettings(request);
        return ApiResponse.success("Payroll Settings Updated Successfully", response);
    }
}
