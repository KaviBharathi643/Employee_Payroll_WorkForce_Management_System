package com.company.payroll.mapper;

import com.company.payroll.dto.attendance.AttendanceResponseDto;
import com.company.payroll.entity.Attendance;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttendanceMapper {

    private final UserProfileRepository userProfileRepository;

    public AttendanceResponseDto toResponse(Attendance attendance, String resolvedStatus) {
        UserProfile profile = userProfileRepository.findByUser(attendance.getUser()).orElse(null);
        return AttendanceResponseDto.builder()
                .attendanceId(attendance.getId())
                .attendanceDate(attendance.getAttendanceDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .status(resolvedStatus != null ? resolvedStatus : attendance.getStatus())
                .userId(attendance.getUser().getId())
                .employeeCode(profile != null ? profile.getEmployeeCode() : null)
                .employeeName(profile != null ? profile.getFullName() : null)
                .build();
    }
}
