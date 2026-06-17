package com.company.payroll.service;

import com.company.payroll.constants.AttendanceConstants;
import com.company.payroll.constants.LeaveConstants;
import com.company.payroll.entity.Attendance;
import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.User;
import com.company.payroll.repository.AttendanceRepository;
import com.company.payroll.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AttendanceStatusService {

    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    @Transactional(readOnly = true)
    public String calculateAttendanceStatus(Long userId, LocalDate attendanceDate) {
        LeaveRequest approvedLeave = leaveRequestRepository.findApprovedLeaveForDate(userId, attendanceDate)
                .orElse(null);

        if (approvedLeave != null) {
            if (LeaveConstants.FULL_DAY.equals(approvedLeave.getDurationType())
                    || LeaveConstants.MULTI_DAY.equals(approvedLeave.getDurationType())) {
                return AttendanceConstants.LEAVE;
            }
            if (LeaveConstants.HALF_DAY.equals(approvedLeave.getDurationType())) {
                return attendanceRepository.findByUserIdAndAttendanceDate(userId, attendanceDate).isPresent()
                        ? AttendanceConstants.HALF_DAY_LEAVE
                        : AttendanceConstants.ABSENT;
            }
        }

        return attendanceRepository.findByUserIdAndAttendanceDate(userId, attendanceDate).isPresent()
                ? AttendanceConstants.PRESENT
                : AttendanceConstants.ABSENT;
    }

    @Transactional
    public void applyStatusAfterCheckIn(User user, LocalDate date, Attendance attendance) {
        String status = calculateAttendanceStatus(user.getId(), date);
        if (AttendanceConstants.ABSENT.equals(status)) {
            status = AttendanceConstants.PRESENT;
        }
        attendance.setStatus(status);
        attendanceRepository.save(attendance);
    }

    @Transactional
    public void recalculateForLeaveChange(User user, LocalDate startDate, LocalDate endDate) {
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            final LocalDate date = current;
            leaveRequestRepository.findApprovedLeaveForDate(user.getId(), date).ifPresent(leave ->
                    attendanceRepository.findByUserIdAndAttendanceDate(user.getId(), date).ifPresent(attendance -> {
                        attendance.setStatus(calculateAttendanceStatus(user.getId(), date));
                        attendanceRepository.save(attendance);
                    }));
            current = current.plusDays(1);
        }
    }
}
