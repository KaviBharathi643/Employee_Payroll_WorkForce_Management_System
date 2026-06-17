package com.company.payroll.dto.auth;

import com.company.payroll.entity.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MeResponseDto {

    private Long userId;
    private String email;
    private String role;
    private String status;
    private String employeeCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phoneNumber;
    private String address;
    private String profilePhotoUrl;
    private String department;
    private String designation;
}
