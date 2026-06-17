package com.company.payroll.dto.employee;

import com.company.payroll.entity.Gender;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class EmployeeResponseDto {

    private Long userId;
    private String employeeCode;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phone;
    private String address;
    private String profilePhotoUrl;
    private String department;
    private String designation;
    private LocalDate joiningDate;
    private String employmentType;
    private BigDecimal basicSalary;
    private String employmentStatus;
    private String userStatus;
    private String bankName;
    private String accountNumber;
    private String maskedAccountNumber;
    private String ifscCode;
}
