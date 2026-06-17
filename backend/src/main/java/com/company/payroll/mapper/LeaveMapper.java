package com.company.payroll.mapper;

import com.company.payroll.dto.leave.LeaveResponseDto;
import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeaveMapper {

    private final UserProfileRepository userProfileRepository;

    public LeaveResponseDto toResponse(LeaveRequest leave) {
        UserProfile applicantProfile = userProfileRepository.findByUser(leave.getUser()).orElse(null);
        UserProfile approverProfile = leave.getApprovedBy() != null
                ? userProfileRepository.findByUser(leave.getApprovedBy()).orElse(null)
                : null;

        return LeaveResponseDto.builder()
                .leaveId(leave.getId())
                .userId(leave.getUser().getId())
                .employeeCode(applicantProfile != null ? applicantProfile.getEmployeeCode() : null)
                .employeeName(applicantProfile != null ? applicantProfile.getFullName() : null)
                .leaveType(leave.getLeaveType())
                .durationType(leave.getDurationType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .leaveDays(leave.getLeaveDays())
                .status(leave.getStatus())
                .reason(leave.getReason())
                .approvedById(leave.getApprovedBy() != null ? leave.getApprovedBy().getId() : null)
                .approvedByName(approverProfile != null ? approverProfile.getFullName() : null)
                .build();
    }
}
