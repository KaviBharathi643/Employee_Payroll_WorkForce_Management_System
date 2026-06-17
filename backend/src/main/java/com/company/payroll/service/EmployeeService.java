package com.company.payroll.service;

import com.company.payroll.constants.RoleConstants;
import com.company.payroll.constants.StatusConstants;
import com.company.payroll.dto.employee.CreateEmployeeRequestDto;
import com.company.payroll.dto.employee.CreateHrRequestDto;
import com.company.payroll.dto.employee.EmployeeListResponseDto;
import com.company.payroll.dto.employee.EmployeeResponseDto;
import com.company.payroll.dto.employee.PagedResponseDto;
import com.company.payroll.dto.employee.UpdateBankDetailsRequestDto;
import com.company.payroll.dto.employee.UpdateEmployeeRequestDto;
import com.company.payroll.dto.employee.UpdateHrRequestDto;
import com.company.payroll.dto.employee.UpdateProfileRequestDto;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserBankDetails;
import com.company.payroll.entity.UserEmployment;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.exception.BusinessRuleException;
import com.company.payroll.exception.ResourceNotFoundException;
import com.company.payroll.exception.ValidationException;
import com.company.payroll.mapper.EmployeeMapper;
import com.company.payroll.repository.EmployeeSpecifications;
import com.company.payroll.repository.UserBankDetailsRepository;
import com.company.payroll.repository.UserEmploymentRepository;
import com.company.payroll.repository.UserProfileRepository;
import com.company.payroll.repository.UserRepository;
import com.company.payroll.security.CurrentUserService;
import com.company.payroll.util.CodeGeneratorUtil;
import com.company.payroll.util.FileStorageUtil;
import com.company.payroll.util.PasswordGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserEmploymentRepository userEmploymentRepository;
    private final UserBankDetailsRepository userBankDetailsRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodeGeneratorUtil codeGeneratorUtil;
    private final PasswordGeneratorUtil passwordGeneratorUtil;
    private final EmployeeMapper employeeMapper;
    private final EmailService emailService;
    private final NotificationHelperService notificationHelperService;
    private final CurrentUserService currentUserService;
    private final FileStorageUtil fileStorageUtil;

    @Transactional
    public EmployeeResponseDto createEmployee(CreateEmployeeRequestDto request) {
        User hr = currentUserService.getCurrentUser();
        if (!RoleConstants.HR.equals(hr.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }

        validateMinimumAge(request.getDateOfBirth());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email Already Exists");
        }

        String tempPassword = passwordGeneratorUtil.generateTemporaryPassword(12);
        User employee = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(tempPassword))
                .role(RoleConstants.EMPLOYEE)
                .status(StatusConstants.ACTIVE)
                .build();
        employee = userRepository.save(employee);

        UserProfile profile = UserProfile.builder()
                .user(employee)
                .employeeCode(codeGeneratorUtil.generateEmployeeCode())
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phoneNumber(request.getPhone())
                .address(request.getAddress())
                .build();
        userProfileRepository.save(profile);

        UserEmployment employment = UserEmployment.builder()
                .user(employee)
                .reportingManager(hr)
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .joiningDate(request.getJoiningDate())
                .employmentType(request.getEmploymentType())
                .basicSalary(request.getBasicSalary())
                .employmentStatus(StatusConstants.ACTIVE)
                .build();
        userEmploymentRepository.save(employment);

        notificationHelperService.notifyAccountCreated(employee);
        emailService.sendAccountCreatedEmail(employee.getEmail(), profile.getFullName(), tempPassword);

        return employeeMapper.toResponse(employee, profile, employment, null, true);
    }

    @Transactional
    public EmployeeResponseDto createHr(CreateHrRequestDto request) {
        User admin = currentUserService.getCurrentUser();
        if (!RoleConstants.ADMIN.equals(admin.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }

        validateMinimumAge(request.getDateOfBirth());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email Already Exists");
        }

        String tempPassword = passwordGeneratorUtil.generateTemporaryPassword(12);
        User hr = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(tempPassword))
                .role(RoleConstants.HR)
                .status(StatusConstants.ACTIVE)
                .build();
        hr = userRepository.save(hr);

        UserProfile profile = UserProfile.builder()
                .user(hr)
                .employeeCode(codeGeneratorUtil.generateHrCode())
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phoneNumber(request.getPhone())
                .address(request.getAddress())
                .build();
        userProfileRepository.save(profile);

        UserEmployment employment = UserEmployment.builder()
                .user(hr)
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .joiningDate(request.getJoiningDate())
                .employmentType("FULL_TIME")
                .basicSalary(request.getBasicSalary())
                .employmentStatus(StatusConstants.ACTIVE)
                .build();
        userEmploymentRepository.save(employment);

        notificationHelperService.notifyAccountCreated(hr);
        emailService.sendAccountCreatedEmail(hr.getEmail(), profile.getFullName(), tempPassword);

        return employeeMapper.toResponse(hr, profile, employment, null, true);
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<EmployeeListResponseDto> getEmployees(
            String search,
            String department,
            String designation,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.HR.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }

        Sort sort = Sort.by("asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy != null && !sortBy.isBlank() ? sortBy : "fullName");
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<UserProfile> spec = EmployeeSpecifications.activeEmployees(search, department, designation);
        Page<UserProfile> profiles = userProfileRepository.findAll(spec, pageable);

        List<EmployeeListResponseDto> content = profiles.getContent().stream()
                .map(profile -> {
                    UserEmployment employment = userEmploymentRepository.findByUser(profile.getUser()).orElse(null);
                    return employeeMapper.toListItem(profile.getUser(), profile, employment);
                })
                .toList();

        return PagedResponseDto.<EmployeeListResponseDto>builder()
                .content(content)
                .page(profiles.getNumber())
                .size(profiles.getSize())
                .totalElements(profiles.getTotalElements())
                .totalPages(profiles.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public List<EmployeeListResponseDto> getHrList() {
        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.ADMIN.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }

        return userProfileRepository.findAll(EmployeeSpecifications.activeHrAccounts()).stream()
                .map(profile -> {
                    UserEmployment employment = userEmploymentRepository.findByUser(profile.getUser()).orElse(null);
                    return employeeMapper.toListItem(profile.getUser(), profile, employment);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeDetails(Long employeeId) {
        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.HR.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }
        return loadUserDetails(employeeId, RoleConstants.EMPLOYEE, true);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto getHrDetails(Long hrId) {
        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.ADMIN.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }
        return loadUserDetails(hrId, RoleConstants.HR, true);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto getMyProfile() {
        User current = currentUserService.getCurrentUser();
        return loadUserDetails(current.getId(), current.getRole(), false);
    }

    @Transactional
    public EmployeeResponseDto updateEmployee(Long employeeId, UpdateEmployeeRequestDto request) {
        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.HR.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }

        User user = userRepository.findByIdAndRole(employeeId, RoleConstants.EMPLOYEE)
                .orElseThrow(() -> new ResourceNotFoundException("Employee Not Found"));
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee Not Found"));
        UserEmployment employment = userEmploymentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee Not Found"));

        profile.setFullName(request.getFullName());
        profile.setPhoneNumber(request.getPhone());
        profile.setAddress(request.getAddress());
        employment.setDepartment(request.getDepartment());
        employment.setDesignation(request.getDesignation());
        employment.setBasicSalary(request.getBasicSalary());
        employment.setEmploymentType(request.getEmploymentType());

        userProfileRepository.save(profile);
        userEmploymentRepository.save(employment);

        UserBankDetails bank = userBankDetailsRepository.findByUser(user).orElse(null);
        return employeeMapper.toResponse(user, profile, employment, bank, true);
    }

    @Transactional
    public EmployeeResponseDto updateHr(Long hrId, UpdateHrRequestDto request) {
        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.ADMIN.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }

        User user = userRepository.findByIdAndRole(hrId, RoleConstants.HR)
                .orElseThrow(() -> new ResourceNotFoundException("HR Not Found"));
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("HR Not Found"));
        UserEmployment employment = userEmploymentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("HR Not Found"));

        profile.setFullName(request.getFullName());
        profile.setPhoneNumber(request.getPhone());
        profile.setAddress(request.getAddress());
        employment.setDepartment(request.getDepartment());
        employment.setDesignation(request.getDesignation());
        employment.setBasicSalary(request.getBasicSalary());

        userProfileRepository.save(profile);
        userEmploymentRepository.save(employment);

        UserBankDetails bank = userBankDetailsRepository.findByUser(user).orElse(null);
        return employeeMapper.toResponse(user, profile, employment, bank, true);
    }

    @Transactional
    public EmployeeResponseDto updateMyProfile(UpdateProfileRequestDto request) {
        User current = currentUserService.getCurrentUser();
        validateMinimumAge(request.getDateOfBirth());

        UserProfile profile = userProfileRepository.findByUser(current)
                .orElseThrow(() -> new ResourceNotFoundException("Profile Not Found"));
        UserEmployment employment = userEmploymentRepository.findByUser(current).orElse(null);

        profile.setFullName(request.getFullName());
        profile.setPhoneNumber(request.getPhone());
        profile.setAddress(request.getAddress());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        userProfileRepository.save(profile);

        UserBankDetails bank = userBankDetailsRepository.findByUser(current).orElse(null);
        return employeeMapper.toResponse(current, profile, employment, bank, false);
    }

    @Transactional
    public EmployeeResponseDto updateBankDetails(UpdateBankDetailsRequestDto request) {
        User current = currentUserService.getCurrentUser();
        if (RoleConstants.ADMIN.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }

        UserBankDetails bank = userBankDetailsRepository.findByUser(current)
                .orElse(UserBankDetails.builder().user(current).build());
        bank.setBankName(request.getBankName());
        bank.setAccountNumber(request.getAccountNumber());
        bank.setIfscCode(request.getIfscCode());
        userBankDetailsRepository.save(bank);

        UserProfile profile = userProfileRepository.findByUser(current).orElse(null);
        UserEmployment employment = userEmploymentRepository.findByUser(current).orElse(null);
        return employeeMapper.toResponse(current, profile, employment, bank, false);
    }

    @Transactional
    public void deactivateEmployee(Long employeeId) {
        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.HR.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }
        deactivateUser(employeeId, RoleConstants.EMPLOYEE, "Employee Not Found");
    }

    @Transactional
    public void deactivateHr(Long hrId) {
        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.ADMIN.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }
        deactivateUser(hrId, RoleConstants.HR, "HR Not Found");
    }

    @Transactional
    public EmployeeResponseDto uploadProfilePhoto(MultipartFile file) {
        User current = currentUserService.getCurrentUser();
        UserProfile profile = userProfileRepository.findByUser(current)
                .orElseThrow(() -> new ResourceNotFoundException("Profile Not Found"));

        if (profile.getProfilePhotoUrl() != null) {
            fileStorageUtil.deleteProfilePhoto(profile.getProfilePhotoUrl());
        }

        String photoUrl = fileStorageUtil.storeProfilePhoto(file);
        profile.setProfilePhotoUrl(photoUrl);
        userProfileRepository.save(profile);

        UserEmployment employment = userEmploymentRepository.findByUser(current).orElse(null);
        UserBankDetails bank = userBankDetailsRepository.findByUser(current).orElse(null);
        return employeeMapper.toResponse(current, profile, employment, bank, false);
    }

    @Transactional(readOnly = true)
    public FileStorageUtil.ProfilePhotoContent downloadProfilePhoto(Long userId) {
        User current = currentUserService.getCurrentUser();
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        validateProfilePhotoAccess(current, target);

        UserProfile profile = userProfileRepository.findByUser(target)
                .orElseThrow(() -> new ResourceNotFoundException("Profile Not Found"));
        return fileStorageUtil.readProfilePhoto(profile.getProfilePhotoUrl());
    }

    @Transactional
    public EmployeeResponseDto deleteProfilePhoto() {
        User current = currentUserService.getCurrentUser();
        UserProfile profile = userProfileRepository.findByUser(current)
                .orElseThrow(() -> new ResourceNotFoundException("Profile Not Found"));

        fileStorageUtil.deleteProfilePhoto(profile.getProfilePhotoUrl());
        profile.setProfilePhotoUrl(null);
        userProfileRepository.save(profile);

        UserEmployment employment = userEmploymentRepository.findByUser(current).orElse(null);
        UserBankDetails bank = userBankDetailsRepository.findByUser(current).orElse(null);
        return employeeMapper.toResponse(current, profile, employment, bank, false);
    }

    private void deactivateUser(Long userId, String role, String notFoundMessage) {
        User user = userRepository.findByIdAndRole(userId, role)
                .orElseThrow(() -> new ResourceNotFoundException(notFoundMessage));
        user.setStatus(StatusConstants.INACTIVE);
        userRepository.save(user);

        userEmploymentRepository.findByUser(user).ifPresent(employment -> {
            employment.setEmploymentStatus(StatusConstants.INACTIVE);
            userEmploymentRepository.save(employment);
        });
    }

    private EmployeeResponseDto loadUserDetails(Long userId, String expectedRole, boolean maskBank) {
        User user = userRepository.findByIdAndRole(userId, expectedRole)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RoleConstants.EMPLOYEE.equals(expectedRole) ? "Employee Not Found" : "HR Not Found"));

        User current = currentUserService.getCurrentUser();
        if (user.getId().equals(current.getId())) {
            maskBank = false;
        } else if (RoleConstants.HR.equals(current.getRole()) && RoleConstants.EMPLOYEE.equals(expectedRole)) {
            maskBank = true;
        } else if (RoleConstants.ADMIN.equals(current.getRole()) && RoleConstants.HR.equals(expectedRole)) {
            maskBank = true;
        } else if (!current.getId().equals(userId)) {
            throw new AccessDeniedException("Access Denied");
        }

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile Not Found"));
        UserEmployment employment = userEmploymentRepository.findByUser(user).orElse(null);
        UserBankDetails bank = userBankDetailsRepository.findByUser(user).orElse(null);
        return employeeMapper.toResponse(user, profile, employment, bank, maskBank);
    }

    private void validateProfilePhotoAccess(User viewer, User target) {
        if (viewer.getId().equals(target.getId())) {
            return;
        }
        if (RoleConstants.EMPLOYEE.equals(viewer.getRole())) {
            throw new AccessDeniedException("Unauthorized Access");
        }
        if (RoleConstants.HR.equals(viewer.getRole())) {
            if (!RoleConstants.EMPLOYEE.equals(target.getRole())) {
                throw new AccessDeniedException("Unauthorized Access");
            }
            return;
        }
        if (RoleConstants.ADMIN.equals(viewer.getRole())) {
            if (!RoleConstants.HR.equals(target.getRole())) {
                throw new AccessDeniedException("Unauthorized Access");
            }
            return;
        }
        throw new AccessDeniedException("Unauthorized Access");
    }

    private void validateMinimumAge(LocalDate dateOfBirth) {
        if (dateOfBirth.plusYears(18).isAfter(LocalDate.now())) {
            throw new ValidationException("Employee must be at least 18 years old");
        }
    }
}
