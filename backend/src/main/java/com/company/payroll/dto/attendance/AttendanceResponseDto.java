package com.company.payroll.dto.attendance;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceResponseDto {

    private Long attendanceId;
    private LocalDate attendanceDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status;
    private Long userId;
    private String employeeCode;
    private String employeeName;
}
