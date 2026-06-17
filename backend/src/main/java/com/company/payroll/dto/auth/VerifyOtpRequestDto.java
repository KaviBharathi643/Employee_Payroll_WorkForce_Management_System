package com.company.payroll.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyOtpRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email format is required")
    private String email;

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
    private String otpCode;
}
