package com.company.payroll.controller;

import com.company.payroll.dto.auth.ForgotPasswordRequestDto;
import com.company.payroll.dto.auth.LoginRequestDto;
import com.company.payroll.dto.auth.LoginResponseDto;
import com.company.payroll.dto.auth.MeResponseDto;
import com.company.payroll.dto.auth.ResetPasswordRequestDto;
import com.company.payroll.dto.auth.VerifyOtpRequestDto;
import com.company.payroll.security.SecurityUtils;
import com.company.payroll.service.AuthService;
import com.company.payroll.service.OtpService;
import com.company.payroll.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ApiResponse.success("Login successful", response);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        otpService.sendOtp(request.getEmail());
        return ApiResponse.success("OTP Sent Successfully");
    }

    @PostMapping("/verify-otp")
    public ApiResponse<Void> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto request) {
        otpService.verifyOtp(request.getEmail(), request.getOtpCode());
        return ApiResponse.success("OTP Verified");
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(request);
        return ApiResponse.success("Password Updated Successfully");
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<MeResponseDto> me() {
        String email = SecurityUtils.getCurrentUserEmail();
        MeResponseDto response = authService.getCurrentUser(email);
        return ApiResponse.success("User retrieved successfully", response);
    }
}
