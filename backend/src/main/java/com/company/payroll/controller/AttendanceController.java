package com.company.payroll.controller;

import com.company.payroll.dto.attendance.AttendanceResponseDto;
import com.company.payroll.dto.attendance.AttendanceSummaryResponseDto;
import com.company.payroll.service.AttendanceService;
import com.company.payroll.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR')")
    public ApiResponse<AttendanceResponseDto> checkIn() {
        AttendanceResponseDto response = attendanceService.checkIn();
        return ApiResponse.success("Check-In Successful", response);
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR')")
    public ApiResponse<AttendanceResponseDto> checkOut() {
        AttendanceResponseDto response = attendanceService.checkOut();
        return ApiResponse.success("Check-Out Successful", response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR')")
    public ApiResponse<List<AttendanceResponseDto>> getMyAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<AttendanceResponseDto> response = attendanceService.getMyAttendance(fromDate, toDate);
        return ApiResponse.success("Attendance Retrieved Successfully", response);
    }

    @GetMapping("/employee/{id}")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ApiResponse<List<AttendanceResponseDto>> getEmployeeAttendance(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<AttendanceResponseDto> response = attendanceService.getEmployeeAttendance(id, fromDate, toDate);
        return ApiResponse.success("Attendance Retrieved Successfully", response);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    public ApiResponse<AttendanceSummaryResponseDto> getAttendanceSummary(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        AttendanceSummaryResponseDto response = attendanceService.getAttendanceSummary(userId, fromDate, toDate);
        return ApiResponse.success("Attendance Summary Generated", response);
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ApiResponse<List<AttendanceResponseDto>> getAttendanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Long userId) {
        List<AttendanceResponseDto> response = attendanceService.getAttendanceReport(fromDate, toDate, userId);
        return ApiResponse.success("Attendance Report Generated Successfully", response);
    }
}
