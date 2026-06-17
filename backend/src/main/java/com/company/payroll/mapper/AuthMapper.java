package com.company.payroll.mapper;

import com.company.payroll.dto.auth.LoginResponseDto;
import com.company.payroll.dto.auth.MeResponseDto;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserEmployment;
import com.company.payroll.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public LoginResponseDto toLoginResponse(String token, User user, UserProfile profile) {
        return LoginResponseDto.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .employeeCode(profile != null ? profile.getEmployeeCode() : null)
                .fullName(profile != null ? profile.getFullName() : null)
                .build();
    }

    public MeResponseDto toMeResponse(User user, UserProfile profile, UserEmployment employment) {
        MeResponseDto.MeResponseDtoBuilder builder = MeResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus());

        if (profile != null) {
            builder.employeeCode(profile.getEmployeeCode())
                    .fullName(profile.getFullName())
                    .dateOfBirth(profile.getDateOfBirth())
                    .gender(profile.getGender())
                    .phoneNumber(profile.getPhoneNumber())
                    .address(profile.getAddress())
                    .profilePhotoUrl(profile.getProfilePhotoUrl());
        }

        if (employment != null) {
            builder.department(employment.getDepartment())
                    .designation(employment.getDesignation());
        }

        return builder.build();
    }
}
