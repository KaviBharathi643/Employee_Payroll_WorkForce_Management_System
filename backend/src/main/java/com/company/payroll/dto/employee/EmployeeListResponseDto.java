package com.company.payroll.dto.employee;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeListResponseDto {

    private Long userId;
    private String employeeCode;
    private String fullName;
    private String email;
    private String department;
    private String designation;
    private String employmentStatus;
}
