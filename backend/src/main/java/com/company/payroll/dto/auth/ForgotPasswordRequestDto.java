package com.company.payroll.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email format is required")
    private String email;
}
