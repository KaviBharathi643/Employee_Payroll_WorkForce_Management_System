package com.company.payroll.service;

import com.company.payroll.constants.RoleConstants;
import com.company.payroll.entity.User;
import com.company.payroll.repository.UserBankDetailsRepository;
import com.company.payroll.repository.UserEmploymentRepository;
import com.company.payroll.repository.UserProfileRepository;
import com.company.payroll.repository.UserRepository;
import com.company.payroll.security.CurrentUserService;
import com.company.payroll.util.FileStorageUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceProfilePhotoAccessTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private UserEmploymentRepository userEmploymentRepository;
    @Mock
    private UserBankDetailsRepository userBankDetailsRepository;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private FileStorageUtil fileStorageUtil;
    @Mock
    private com.company.payroll.mapper.EmployeeMapper employeeMapper;
    @Mock
    private com.company.payroll.util.CodeGeneratorUtil codeGeneratorUtil;
    @Mock
    private NotificationHelperService notificationHelperService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void employeeCanAccessOwnPhoto() {
        User employee = user(1L, RoleConstants.EMPLOYEE);
        when(currentUserService.getCurrentUser()).thenReturn(employee);
        when(userRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(userProfileRepository.findByUser(employee)).thenReturn(Optional.of(
                com.company.payroll.entity.UserProfile.builder().profilePhotoUrl("/uploads/profile/test.jpg").build()));
        when(fileStorageUtil.readProfilePhoto("/uploads/profile/test.jpg"))
                .thenReturn(new FileStorageUtil.ProfilePhotoContent(new byte[]{1}, "image/jpeg"));

        assertDoesNotThrow(() -> employeeService.downloadProfilePhoto(1L));
    }

    @Test
    void employeeCannotAccessOtherPhoto() {
        User employee = user(1L, RoleConstants.EMPLOYEE);
        User other = user(2L, RoleConstants.EMPLOYEE);
        when(currentUserService.getCurrentUser()).thenReturn(employee);
        when(userRepository.findById(2L)).thenReturn(Optional.of(other));

        assertThrows(AccessDeniedException.class, () -> employeeService.downloadProfilePhoto(2L));
    }

    @Test
    void hrCanAccessEmployeePhoto() {
        User hr = user(10L, RoleConstants.HR);
        User employee = user(2L, RoleConstants.EMPLOYEE);
        when(currentUserService.getCurrentUser()).thenReturn(hr);
        when(userRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(userProfileRepository.findByUser(employee)).thenReturn(Optional.of(
                com.company.payroll.entity.UserProfile.builder().profilePhotoUrl("/uploads/profile/test.jpg").build()));
        when(fileStorageUtil.readProfilePhoto("/uploads/profile/test.jpg"))
                .thenReturn(new FileStorageUtil.ProfilePhotoContent(new byte[]{1}, "image/jpeg"));

        assertDoesNotThrow(() -> employeeService.downloadProfilePhoto(2L));
    }

    @Test
    void hrCannotAccessHrPhoto() {
        User hr = user(10L, RoleConstants.HR);
        User otherHr = user(11L, RoleConstants.HR);
        when(currentUserService.getCurrentUser()).thenReturn(hr);
        when(userRepository.findById(11L)).thenReturn(Optional.of(otherHr));

        assertThrows(AccessDeniedException.class, () -> employeeService.downloadProfilePhoto(11L));
    }

    @Test
    void adminCanAccessHrPhoto() {
        User admin = user(20L, RoleConstants.ADMIN);
        User hr = user(11L, RoleConstants.HR);
        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(userRepository.findById(11L)).thenReturn(Optional.of(hr));
        when(userProfileRepository.findByUser(hr)).thenReturn(Optional.of(
                com.company.payroll.entity.UserProfile.builder().profilePhotoUrl("/uploads/profile/test.jpg").build()));
        when(fileStorageUtil.readProfilePhoto("/uploads/profile/test.jpg"))
                .thenReturn(new FileStorageUtil.ProfilePhotoContent(new byte[]{1}, "image/jpeg"));

        assertDoesNotThrow(() -> employeeService.downloadProfilePhoto(11L));
    }

    @Test
    void adminCannotAccessEmployeePhoto() {
        User admin = user(20L, RoleConstants.ADMIN);
        User employee = user(2L, RoleConstants.EMPLOYEE);
        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(userRepository.findById(2L)).thenReturn(Optional.of(employee));

        assertThrows(AccessDeniedException.class, () -> employeeService.downloadProfilePhoto(2L));
    }

    private User user(Long id, String role) {
        return User.builder().id(id).role(role).email(id + "@test.com").build();
    }
}
