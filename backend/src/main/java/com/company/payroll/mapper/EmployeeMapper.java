package com.company.payroll.mapper;

import com.company.payroll.dto.employee.EmployeeListResponseDto;
import com.company.payroll.dto.employee.EmployeeResponseDto;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserBankDetails;
import com.company.payroll.entity.UserEmployment;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.util.BankMaskingUtil;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponseDto toResponse(
            User user,
            UserProfile profile,
            UserEmployment employment,
            UserBankDetails bank,
            boolean maskBank) {

        EmployeeResponseDto.EmployeeResponseDtoBuilder builder = EmployeeResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .userStatus(user.getStatus());

        if (profile != null) {
            builder.employeeCode(profile.getEmployeeCode())
                    .fullName(profile.getFullName())
                    .dateOfBirth(profile.getDateOfBirth())
                    .gender(profile.getGender())
                    .phone(profile.getPhoneNumber())
                    .address(profile.getAddress())
                    .profilePhotoUrl(profile.getProfilePhotoUrl());
        }

        if (employment != null) {
            builder.department(employment.getDepartment())
                    .designation(employment.getDesignation())
                    .joiningDate(employment.getJoiningDate())
                    .employmentType(employment.getEmploymentType())
                    .basicSalary(employment.getBasicSalary())
                    .employmentStatus(employment.getEmploymentStatus());
        }

        if (bank != null) {
            builder.bankName(bank.getBankName())
                    .ifscCode(bank.getIfscCode());
            if (maskBank) {
                builder.maskedAccountNumber(BankMaskingUtil.maskAccountNumber(bank.getAccountNumber()));
            } else {
                builder.accountNumber(bank.getAccountNumber());
            }
        }

        return builder.build();
    }

    public EmployeeListResponseDto toListItem(User user, UserProfile profile, UserEmployment employment) {
        return EmployeeListResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .employeeCode(profile != null ? profile.getEmployeeCode() : null)
                .fullName(profile != null ? profile.getFullName() : null)
                .department(employment != null ? employment.getDepartment() : null)
                .designation(employment != null ? employment.getDesignation() : null)
                .employmentStatus(employment != null ? employment.getEmploymentStatus() : null)
                .build();
    }
}
