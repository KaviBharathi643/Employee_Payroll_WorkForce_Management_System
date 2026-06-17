package com.company.payroll.service;

import com.company.payroll.entity.OtpVerification;
import com.company.payroll.entity.User;
import com.company.payroll.exception.BusinessRuleException;
import com.company.payroll.exception.ResourceNotFoundException;
import com.company.payroll.exception.ValidationException;
import com.company.payroll.repository.OtpVerificationRepository;
import com.company.payroll.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final int OTP_EXPIRY_MINUTES = 2;
    private static final int MAX_OTP_REQUESTS_PER_WINDOW = 5;
    private static final int RATE_LIMIT_WINDOW_MINUTES = 15;

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        enforceRateLimit(user);

        String otpCode = generateOtpCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        OtpVerification otp = OtpVerification.builder()
                .user(user)
                .otpCode(otpCode)
                .expiresAt(expiresAt)
                .build();
        otpVerificationRepository.save(otp);

        emailService.sendOtpEmail(user.getEmail(), otpCode);
    }

    @Transactional(readOnly = true)
    public void verifyOtp(String email, String otpCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Invalid OTP"));

        OtpVerification otpRecord = otpVerificationRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new ValidationException("Invalid OTP"));

        if (LocalDateTime.now().isAfter(otpRecord.getExpiresAt())) {
            throw new ValidationException("OTP Expired");
        }

        if (!otpRecord.getOtpCode().equals(otpCode)) {
            throw new ValidationException("Invalid OTP");
        }
    }

    @Transactional
    public void deleteOtpsForUser(Long userId) {
        otpVerificationRepository.deleteByUserId(userId);
    }

    private void enforceRateLimit(User user) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(RATE_LIMIT_WINDOW_MINUTES);
        long count = otpVerificationRepository.countByUserAndCreatedAtAfter(user, since);
        if (count >= MAX_OTP_REQUESTS_PER_WINDOW) {
            throw new BusinessRuleException("Too many OTP requests. Please try again later.");
        }
    }

    private String generateOtpCode() {
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }
}
