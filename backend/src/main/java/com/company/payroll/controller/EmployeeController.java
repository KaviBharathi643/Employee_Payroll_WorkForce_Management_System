package com.company.payroll.controller;

import com.company.payroll.dto.employee.CreateEmployeeRequestDto;
import com.company.payroll.dto.employee.CreateHrRequestDto;
import com.company.payroll.dto.employee.EmployeeListResponseDto;
import com.company.payroll.dto.employee.EmployeeResponseDto;
import com.company.payroll.dto.employee.PagedResponseDto;
import com.company.payroll.dto.employee.UpdateBankDetailsRequestDto;
import com.company.payroll.dto.employee.UpdateEmployeeRequestDto;
import com.company.payroll.dto.employee.UpdateHrRequestDto;
import com.company.payroll.dto.employee.UpdateProfileRequestDto;
import com.company.payroll.service.EmployeeService;
import com.company.payroll.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ApiResponse<EmployeeResponseDto> createEmployee(@Valid @RequestBody CreateEmployeeRequestDto request) {
        EmployeeResponseDto response = employeeService.createEmployee(request);
        return ApiResponse.success("Employee Created Successfully", response);
    }

    @PostMapping("/hr")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<EmployeeResponseDto> createHr(@Valid @RequestBody CreateHrRequestDto request) {
        EmployeeResponseDto response = employeeService.createHr(request);
        return ApiResponse.success("HR Created Successfully", response);
    }

    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public ApiResponse<PagedResponseDto<EmployeeListResponseDto>> getEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedResponseDto<EmployeeListResponseDto> response = employeeService.getEmployees(
                search, department, designation, page, size, sortBy, sortDir);
        return ApiResponse.success("Employees retrieved successfully", response);
    }

    @GetMapping("/hr")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<EmployeeListResponseDto>> getHrList() {
        List<EmployeeListResponseDto> response = employeeService.getHrList();
        return ApiResponse.success("HR list retrieved successfully", response);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    public ApiResponse<EmployeeResponseDto> getMyProfile() {
        EmployeeResponseDto response = employeeService.getMyProfile();
        return ApiResponse.success("Profile retrieved successfully", response);
    }

    @GetMapping("/hr/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<EmployeeResponseDto> getHrDetails(@PathVariable Long id) {
        EmployeeResponseDto response = employeeService.getHrDetails(id);
        return ApiResponse.success("HR details retrieved successfully", response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ApiResponse<EmployeeResponseDto> getEmployeeDetails(@PathVariable Long id) {
        EmployeeResponseDto response = employeeService.getEmployeeDetails(id);
        return ApiResponse.success("Employee details retrieved successfully", response);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    public ApiResponse<EmployeeResponseDto> updateMyProfile(@Valid @RequestBody UpdateProfileRequestDto request) {
        EmployeeResponseDto response = employeeService.updateMyProfile(request);
        return ApiResponse.success("Profile updated successfully", response);
    }

    @PutMapping("/bank-details")
    @PreAuthorize("hasAnyRole('HR','EMPLOYEE')")
    public ApiResponse<EmployeeResponseDto> updateBankDetails(@Valid @RequestBody UpdateBankDetailsRequestDto request) {
        EmployeeResponseDto response = employeeService.updateBankDetails(request);
        return ApiResponse.success("Bank Details Updated Successfully", response);
    }

    @PutMapping("/hr/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<EmployeeResponseDto> updateHr(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHrRequestDto request) {
        EmployeeResponseDto response = employeeService.updateHr(id, request);
        return ApiResponse.success("HR Updated Successfully", response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ApiResponse<EmployeeResponseDto> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequestDto request) {
        EmployeeResponseDto response = employeeService.updateEmployee(id, request);
        return ApiResponse.success("Employee Updated Successfully", response);
    }

    @PatchMapping("/hr/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deactivateHr(@PathVariable Long id) {
        employeeService.deactivateHr(id);
        return ApiResponse.success("HR Deactivated Successfully");
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('HR')")
    public ApiResponse<Void> deactivateEmployee(@PathVariable Long id) {
        employeeService.deactivateEmployee(id);
        return ApiResponse.success("Employee Deactivated Successfully");
    }
}
