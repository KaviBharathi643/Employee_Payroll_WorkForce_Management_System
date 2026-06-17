package com.company.payroll.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {

    private String token;
    private Long userId;
    private String employeeCode;
    private String fullName;
    private String email;
    private String role;
}
