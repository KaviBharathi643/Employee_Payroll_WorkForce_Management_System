package com.company.payroll.service;

import com.company.payroll.constants.AttendanceConstants;
import com.company.payroll.constants.LeaveConstants;
import com.company.payroll.constants.NotificationConstants;
import com.company.payroll.constants.RoleConstants;
import com.company.payroll.constants.StatusConstants;
import com.company.payroll.dto.attendance.AttendanceResponseDto;
import com.company.payroll.dto.attendance.AttendanceSummaryResponseDto;
import com.company.payroll.entity.Attendance;
import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.SystemSettings;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.exception.BusinessRuleException;
import com.company.payroll.exception.ResourceNotFoundException;
import com.company.payroll.mapper.AttendanceMapper;
import com.company.payroll.repository.AttendanceRepository;
import com.company.payroll.repository.LeaveRequestRepository;
import com.company.payroll.repository.NotificationRepository;
import com.company.payroll.repository.UserProfileRepository;
import com.company.payroll.repository.UserRepository;
import com.company.payroll.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private static final String MISSING_CHECKOUT_PENALTY_PREFIX = "Missing Checkout Penalty";

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final NotificationRepository notificationRepository;
    private final AttendanceMapper attendanceMapper;
    private final AttendanceStatusService attendanceStatusService;
    private final CurrentUserService currentUserService;
    private final SystemSettingsService systemSettingsService;
    private final NotificationHelperService notificationHelperService;
    private final EmailService emailService;

    @Transactional
    public AttendanceResponseDto checkIn() {
        User employee = currentUserService.getCurrentUser();
        if (!RoleConstants.EMPLOYEE.equals(employee.getRole()) && !RoleConstants.HR.equals(employee.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }

        LocalDate today = LocalDate.now();
        if (isWeekend(today)) {
            throw new BusinessRuleException("Attendance Not Required On Holidays");
        }

        if (attendanceRepository.findByUserIdAndAttendanceDate(employee.getId(), today).isPresent()) {
            throw new BusinessRuleException("Already Checked In");
        }

        Attendance attendance = Attendance.builder()
                .user(employee)
                .attendanceDate(today)
                .checkInTime(LocalDateTime.now())
                .status(AttendanceConstants.PRESENT)
                .build();
        attendance = attendanceRepository.save(attendance);
        attendanceStatusService.applyStatusAfterCheckIn(employee, today, attendance);
        attendance = attendanceRepository.findById(attendance.getId()).orElse(attendance);

        String status = attendanceStatusService.calculateAttendanceStatus(employee.getId(), today);
        return attendanceMapper.toResponse(attendance, status);
    }

    @Transactional
    public AttendanceResponseDto checkOut() {
        User employee = currentUserService.getCurrentUser();
        if (!RoleConstants.EMPLOYEE.equals(employee.getRole()) && !RoleConstants.HR.equals(employee.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }

        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserIdAndAttendanceDate(employee.getId(), today)
                .orElseThrow(() -> new BusinessRuleException("Check In Required"));

        if (attendance.getCheckOutTime() != null) {
            throw new BusinessRuleException("Already Checked Out");
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        attendance = attendanceRepository.save(attendance);

        String status = attendanceStatusService.calculateAttendanceStatus(employee.getId(), today);
        return attendanceMapper.toResponse(attendance, status);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getMyAttendance(LocalDate fromDate, LocalDate toDate) {
        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.EMPLOYEE.equals(current.getRole()) && !RoleConstants.HR.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }
        return getAttendanceForUser(current.getId(), fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getEmployeeAttendance(Long employeeId, LocalDate fromDate, LocalDate toDate) {
        User current = currentUserService.getCurrentUser();
        if (RoleConstants.HR.equals(current.getRole())) {
            userRepository.findByIdAndRole(employeeId, RoleConstants.EMPLOYEE)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee Not Found"));
        } else if (RoleConstants.ADMIN.equals(current.getRole())) {
            userRepository.findByIdAndRole(employeeId, RoleConstants.HR)
                    .orElseThrow(() -> new ResourceNotFoundException("HR Not Found"));
        } else {
            throw new AccessDeniedException("Access Denied");
        }
        return getAttendanceForUser(employeeId, fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public AttendanceSummaryResponseDto getAttendanceSummary(Long userId, LocalDate fromDate, LocalDate toDate) {
        User current = currentUserService.getCurrentUser();
        LocalDate rangeStart = fromDate != null ? fromDate : YearMonth.now().atDay(1);
        LocalDate rangeEnd = toDate != null ? toDate : LocalDate.now();

        if (RoleConstants.EMPLOYEE.equals(current.getRole())) {
            return buildUserSummary(current.getId(), rangeStart, rangeEnd, LocalDate.now());
        }
        if (RoleConstants.HR.equals(current.getRole())) {
            if (userId != null && !current.getId().equals(userId)) {
                userRepository.findByIdAndRole(userId, RoleConstants.EMPLOYEE)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee Not Found"));
                return buildUserSummary(userId, rangeStart, rangeEnd, LocalDate.now());
            }
            if (userId != null && current.getId().equals(userId)) {
                return buildUserSummary(current.getId(), rangeStart, rangeEnd, LocalDate.now());
            }
            return buildRoleAggregateSummary(RoleConstants.EMPLOYEE, rangeStart, rangeEnd);
        }
        if (RoleConstants.ADMIN.equals(current.getRole())) {
            if (userId != null) {
                userRepository.findByIdAndRole(userId, RoleConstants.HR)
                        .orElseThrow(() -> new ResourceNotFoundException("HR Not Found"));
                return buildUserSummary(userId, rangeStart, rangeEnd, LocalDate.now());
            }
            return buildRoleAggregateSummary(RoleConstants.HR, rangeStart, rangeEnd);
        }
        throw new AccessDeniedException("Access Denied");
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getAttendanceReport(LocalDate fromDate, LocalDate toDate, Long userId) {
        User current = currentUserService.getCurrentUser();
        if (RoleConstants.HR.equals(current.getRole())) {
            if (userId != null) {
                return getAttendanceForUser(userId, fromDate, toDate);
            }
            return listAttendanceForRole(RoleConstants.EMPLOYEE, fromDate, toDate);
        }
        if (RoleConstants.ADMIN.equals(current.getRole())) {
            if (userId != null) {
                userRepository.findByIdAndRole(userId, RoleConstants.HR)
                        .orElseThrow(() -> new ResourceNotFoundException("HR Not Found"));
                return getAttendanceForUser(userId, fromDate, toDate);
            }
            return listAttendanceForRole(RoleConstants.HR, fromDate, toDate);
        }
        throw new AccessDeniedException("Access Denied");
    }

    @Transactional
    public void processMissingCheckoutReminders() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        SystemSettings settings = systemSettingsService.requireSettings();

        if (now.isBefore(settings.getCheckoutReminderTime())) {
            return;
        }

        List<Attendance> missingCheckout = attendanceRepository.findByAttendanceDateWithoutCheckout(today);
        List<User> hrUsers = userRepository.findByRoleAndStatus(RoleConstants.HR, StatusConstants.ACTIVE);

        for (Attendance attendance : missingCheckout) {
            User user = attendance.getUser();
            if (!RoleConstants.EMPLOYEE.equals(user.getRole()) && !RoleConstants.HR.equals(user.getRole())) {
                continue;
            }

            String redirectType = NotificationConstants.REDIRECT_ATTENDANCE + ":" + attendance.getId();

            if (!notificationRepository.existsByUserIdAndRedirectType(user.getId(), redirectType)) {
                notificationHelperService.notifyMissingCheckout(user, attendance.getId());
                emailService.sendMissingCheckoutEmail(user.getEmail());
            }

            String userName = userProfileRepository.findByUser(user)
                    .map(UserProfile::getFullName)
                    .orElse(user.getEmail());
            for (User hr : hrUsers) {
                if (!notificationRepository.existsByUserIdAndRedirectType(hr.getId(), redirectType)) {
                    notificationHelperService.notifyHrMissingCheckout(hr, userName, attendance.getId());
                    emailService.sendMissingCheckoutEmail(hr.getEmail());
                }
            }

            applyMissingCheckoutPenalty(user, today);
        }
    }

    private void applyMissingCheckoutPenalty(User employee, LocalDate date) {
        if (leaveRequestRepository.existsMissingCheckoutPenalty(employee.getId(), date)) {
            return;
        }

        LeaveRequest penalty = LeaveRequest.builder()
                .user(employee)
                .leaveType(LeaveConstants.UNPAID)
                .durationType(LeaveConstants.HALF_DAY)
                .startDate(date)
                .endDate(date)
                .leaveDays(BigDecimal.valueOf(0.5))
                .reason(MISSING_CHECKOUT_PENALTY_PREFIX)
                .status(LeaveConstants.APPROVED)
                .build();
        leaveRequestRepository.save(penalty);
    }

    private List<AttendanceResponseDto> getAttendanceForUser(Long userId, LocalDate fromDate, LocalDate toDate) {
        LocalDate start = fromDate != null ? fromDate : YearMonth.now().atDay(1);
        LocalDate end = toDate != null ? toDate : LocalDate.now();

        return attendanceRepository.findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(userId, start, end)
                .stream()
                .map(attendance -> {
                    String status = attendanceStatusService.calculateAttendanceStatus(
                            userId, attendance.getAttendanceDate());
                    return attendanceMapper.toResponse(attendance, status);
                })
                .toList();
    }

    private List<AttendanceResponseDto> listAttendanceForRole(String role, LocalDate fromDate, LocalDate toDate) {
        List<User> users = userRepository.findByRoleAndStatus(role, StatusConstants.ACTIVE);
        List<AttendanceResponseDto> result = new ArrayList<>();
        for (User user : users) {
            result.addAll(getAttendanceForUser(user.getId(), fromDate, toDate));
        }
        return result;
    }

    private AttendanceSummaryResponseDto buildUserSummary(
            Long userId, LocalDate fromDate, LocalDate toDate, LocalDate missingCheckoutDate) {
        long present = 0;
        long absent = 0;
        long leave = 0;
        long halfDay = 0;

        LocalDate current = fromDate;
        while (!current.isAfter(toDate)) {
            if (!isWeekend(current)) {
                String status = attendanceStatusService.calculateAttendanceStatus(userId, current);
                switch (status) {
                    case AttendanceConstants.PRESENT -> present++;
                    case AttendanceConstants.ABSENT -> absent++;
                    case AttendanceConstants.LEAVE -> leave++;
                    case AttendanceConstants.HALF_DAY_LEAVE -> halfDay++;
                    default -> {
                    }
                }
            }
            current = current.plusDays(1);
        }

        long missingCheckout = attendanceRepository.findByUserIdAndAttendanceDate(userId, missingCheckoutDate)
                .filter(a -> a.getCheckInTime() != null && a.getCheckOutTime() == null)
                .isPresent() ? 1 : 0;

        return AttendanceSummaryResponseDto.builder()
                .presentCount(present)
                .absentCount(absent)
                .leaveCount(leave)
                .halfDayLeaveCount(halfDay)
                .missingCheckoutCount(missingCheckout)
                .build();
    }

    private AttendanceSummaryResponseDto buildRoleAggregateSummary(
            String role, LocalDate fromDate, LocalDate toDate) {
        List<User> users = userRepository.findByRoleAndStatus(role, StatusConstants.ACTIVE);
        long present = 0;
        long absent = 0;
        long leave = 0;
        long halfDay = 0;

        for (User user : users) {
            AttendanceSummaryResponseDto userSummary = buildUserSummary(
                    user.getId(), fromDate, toDate, LocalDate.now());
            present += userSummary.getPresentCount();
            absent += userSummary.getAbsentCount();
            leave += userSummary.getLeaveCount();
            halfDay += userSummary.getHalfDayLeaveCount();
        }

        long missingCheckout = attendanceRepository.countMissingCheckoutByRoleAndDate(role, LocalDate.now());

        return AttendanceSummaryResponseDto.builder()
                .presentCount(present)
                .absentCount(absent)
                .leaveCount(leave)
                .halfDayLeaveCount(halfDay)
                .missingCheckoutCount(missingCheckout)
                .build();
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
