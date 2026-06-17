package com.company.payroll.service;

import com.company.payroll.constants.AttendanceConstants;
import com.company.payroll.constants.LeaveConstants;
import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.User;
import com.company.payroll.repository.AttendanceRepository;
import com.company.payroll.repository.LeaveRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceStatusServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @InjectMocks
    private AttendanceStatusService attendanceStatusService;

    @Test
    void fullDayLeaveOverridesAttendance() {
        Long userId = 1L;
        LocalDate date = LocalDate.of(2026, 6, 4);
        LeaveRequest leave = LeaveRequest.builder().durationType(LeaveConstants.FULL_DAY).build();
        when(leaveRequestRepository.findApprovedLeaveForDate(userId, date)).thenReturn(Optional.of(leave));

        String status = attendanceStatusService.calculateAttendanceStatus(userId, date);
        assertEquals(AttendanceConstants.LEAVE, status);
    }

    @Test
    void noLeaveNoAttendanceIsAbsent() {
        Long userId = 1L;
        LocalDate date = LocalDate.of(2026, 6, 4);
        when(leaveRequestRepository.findApprovedLeaveForDate(userId, date)).thenReturn(Optional.empty());
        when(attendanceRepository.findByUserIdAndAttendanceDate(userId, date)).thenReturn(Optional.empty());

        String status = attendanceStatusService.calculateAttendanceStatus(userId, date);
        assertEquals(AttendanceConstants.ABSENT, status);
    }
}
