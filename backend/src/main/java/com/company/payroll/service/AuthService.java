package com.company.payroll.service;

import com.company.payroll.constants.StatusConstants;
import com.company.payroll.dto.auth.LoginRequestDto;
import com.company.payroll.dto.auth.LoginResponseDto;
import com.company.payroll.dto.auth.MeResponseDto;
import com.company.payroll.dto.auth.ResetPasswordRequestDto;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserEmployment;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.exception.ResourceNotFoundException;
import com.company.payroll.exception.UnauthorizedException;
import com.company.payroll.exception.ValidationException;
import com.company.payroll.mapper.AuthMapper;
import com.company.payroll.repository.UserEmploymentRepository;
import com.company.payroll.repository.UserProfileRepository;
import com.company.payroll.repository.UserRepository;
import com.company.payroll.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserEmploymentRepository userEmploymentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthMapper authMapper;
    private final OtpService otpService;

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {
        log.info("[LOGIN-DEBUG] start email={}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid Email Or Password"));
        log.info("[LOGIN-DEBUG] user loaded id={}", user.getId());

        if (!StatusConstants.ACTIVE.equals(user.getStatus())) {
            throw new UnauthorizedException("Account Is Inactive");
        }
        log.info("[LOGIN-DEBUG] status check passed");

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid Email Or Password");
        }
        log.info("[LOGIN-DEBUG] password verified");

        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        log.info("[LOGIN-DEBUG] profile loaded present={}", profile != null);
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        log.info("[LOGIN-DEBUG] token generated");
        return authMapper.toLoginResponse(token, user, profile);
    }

    @Transactional(readOnly = true)
    public MeResponseDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        UserEmployment employment = userEmploymentRepository.findByUser(user).orElse(null);
        return authMapper.toMeResponse(user, profile, employment);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {
        otpService.verifyOtp(request.getEmail(), request.getOtpCode());

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        otpService.deleteOtpsForUser(user.getId());
    }
}
