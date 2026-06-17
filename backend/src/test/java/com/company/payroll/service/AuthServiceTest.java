package com.company.payroll.service;

import com.company.payroll.constants.RoleConstants;
import com.company.payroll.constants.StatusConstants;
import com.company.payroll.dto.auth.LoginRequestDto;
import com.company.payroll.dto.auth.LoginResponseDto;
import com.company.payroll.entity.Gender;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.exception.UnauthorizedException;
import com.company.payroll.mapper.AuthMapper;
import com.company.payroll.repository.UserEmploymentRepository;
import com.company.payroll.repository.UserProfileRepository;
import com.company.payroll.repository.UserRepository;
import com.company.payroll.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private UserEmploymentRepository userEmploymentRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuthMapper authMapper;
    @Mock
    private OtpService otpService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_success() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("admin@company.com");
        request.setPassword("Admin@123456");

        User user = User.builder()
                .id(1L)
                .email(request.getEmail())
                .password("encoded")
                .role(RoleConstants.ADMIN)
                .status(StatusConstants.ACTIVE)
                .build();

        UserProfile profile = UserProfile.builder()
                .employeeCode("ADM001")
                .fullName("Admin")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.OTHER)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(userProfileRepository.findByUser(user)).thenReturn(Optional.of(profile));
        when(jwtTokenProvider.generateToken(anyLong(), anyString(), anyString())).thenReturn("jwt-token");
        when(authMapper.toLoginResponse(anyString(), any(User.class), any(UserProfile.class)))
                .thenReturn(LoginResponseDto.builder().token("jwt-token").role(RoleConstants.ADMIN).build());

        LoginResponseDto response = authService.login(request);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_inactiveAccount() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("inactive@company.com");
        request.setPassword("password");

        User user = User.builder()
                .email(request.getEmail())
                .password("encoded")
                .status(StatusConstants.INACTIVE)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}
