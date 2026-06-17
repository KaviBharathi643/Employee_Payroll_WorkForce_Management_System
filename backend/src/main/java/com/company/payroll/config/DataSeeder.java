package com.company.payroll.config;

import com.company.payroll.constants.RoleConstants;
import com.company.payroll.constants.StatusConstants;
import com.company.payroll.entity.Gender;
import com.company.payroll.entity.SystemSettings;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserEmployment;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.repository.SystemSettingsRepository;
import com.company.payroll.repository.UserEmploymentRepository;
import com.company.payroll.repository.UserProfileRepository;
import com.company.payroll.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserEmploymentRepository userEmploymentRepository;
    private final SystemSettingsRepository systemSettingsRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-email}")
    private String adminEmail;

    @Value("${app.seed.admin-password}")
    private String adminPassword;

    @Value("${app.seed.admin-full-name}")
    private String adminFullName;

    @Value("${app.seed.admin-employee-code}")
    private String adminEmployeeCode;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedSystemSettings();
        seedAdminUser();
    }

    private void seedSystemSettings() {
        if (systemSettingsRepository.count() > 0) {
            return;
        }
        LocalTime officeStart = LocalTime.of(9, 0);
        LocalTime officeEnd = LocalTime.of(18, 0);
        SystemSettings settings = SystemSettings.builder()
                .companyName("")
                .companyAddress("")
                .companyEmail("noreply@company.com")
                .companyPhone("")
                .officeStartTime(officeStart)
                .officeEndTime(officeEnd)
                .checkoutReminderTime(officeEnd.plusHours(1))
                .annualPaidLeaveLimit(25)
                .pfPercentage(new BigDecimal("8.00"))
                .salaryCreditDay(1)
                .build();
        systemSettingsRepository.save(settings);
        log.info("Default system settings initialized");
    }

    private void seedAdminUser() {
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        User admin = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(RoleConstants.ADMIN)
                .status(StatusConstants.ACTIVE)
                .build();
        admin = userRepository.save(admin);

        UserProfile profile = UserProfile.builder()
                .user(admin)
                .employeeCode(adminEmployeeCode)
                .fullName(adminFullName)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.OTHER)
                .phoneNumber("0000000000")
                .address("System Address")
                .build();
        userProfileRepository.save(profile);

        UserEmployment employment = UserEmployment.builder()
                .user(admin)
                .department("Administration")
                .designation("System Administrator")
                .joiningDate(LocalDate.now())
                .employmentType("FULL_TIME")
                .basicSalary(BigDecimal.ZERO)
                .employmentStatus(StatusConstants.ACTIVE)
                .build();
        userEmploymentRepository.save(employment);

        log.info("Default ADMIN user seeded: {}", adminEmail);
    }
}
