package com.company.payroll.service;

import com.company.payroll.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AttendanceStatusRecalculationService {

    private final AttendanceStatusService attendanceStatusService;

    @Transactional
    public void recalculateForLeaveChange(User user, LocalDate startDate, LocalDate endDate) {
        attendanceStatusService.recalculateForLeaveChange(user, startDate, endDate);
    }
}
