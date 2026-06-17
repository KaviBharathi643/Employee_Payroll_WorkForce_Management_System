package com.company.payroll.service;

import com.company.payroll.constants.LeaveConstants;
import com.company.payroll.constants.PayrollConstants;
import com.company.payroll.constants.RoleConstants;
import com.company.payroll.constants.StatusConstants;
import com.company.payroll.dto.attendance.AttendanceSummaryResponseDto;
import com.company.payroll.dto.report.DashboardAnalyticsResponseDto;
import com.company.payroll.dto.report.DashboardPayrollSummaryDto;
import com.company.payroll.dto.report.LeaveSummaryResponseDto;
import com.company.payroll.dto.report.UserCountSummaryDto;
import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.Payroll;
import com.company.payroll.entity.User;
import com.company.payroll.repository.LeaveRequestRepository;
import com.company.payroll.repository.PayrollRepository;
import com.company.payroll.repository.UserRepository;
import com.company.payroll.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardAnalyticsService {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRepository payrollRepository;
    private final AttendanceService attendanceService;
    private final SystemSettingsService systemSettingsService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public DashboardAnalyticsResponseDto getDashboardAnalytics() {
        User current = requireHrOrAdmin();

        DashboardAnalyticsResponseDto.DashboardAnalyticsResponseDtoBuilder builder =
                DashboardAnalyticsResponseDto.builder()
                        .attendanceSummary(getAttendanceSummary(null, null))
                        .leaveSummary(getLeaveSummary())
                        .payrollSummary(getPayrollSummary());

        if (RoleConstants.HR.equals(current.getRole())) {
            builder.employeeSummary(getUserCountSummary(RoleConstants.EMPLOYEE));
        } else {
            builder.hrSummary(getUserCountSummary(RoleConstants.HR));
        }

        return builder.build();
    }

    @Transactional(readOnly = true)
    public UserCountSummaryDto getUserCountSummary(String role) {
        requireHrOrAdmin();
        long active = userRepository.findByRoleAndStatus(role, StatusConstants.ACTIVE).size();
        long inactive = userRepository.findByRoleAndStatus(role, StatusConstants.INACTIVE).size();
        return UserCountSummaryDto.builder()
                .totalUsers(active + inactive)
                .activeUsers(active)
                .inactiveUsers(inactive)
                .build();
    }

    @Transactional(readOnly = true)
    public AttendanceSummaryResponseDto getAttendanceSummary(LocalDate fromDate, LocalDate toDate) {
        requireHrOrAdmin();
        LocalDate from = fromDate != null ? fromDate : YearMonth.now().atDay(1);
        LocalDate to = toDate != null ? toDate : LocalDate.now();
        return attendanceService.getAttendanceSummary(null, from, to);
    }

    @Transactional(readOnly = true)
    public LeaveSummaryResponseDto getLeaveSummary() {
        User current = requireHrOrAdmin();
        String targetRole = resolveTargetRole(current);
        List<User> users = usersInRole(targetRole);

        long pending = 0;
        long approved = 0;
        long rejected = 0;
        long cancelled = 0;
        BigDecimal paidUsed = BigDecimal.ZERO;
        BigDecimal unpaidUsed = BigDecimal.ZERO;
        BigDecimal remaining = BigDecimal.ZERO;
        int year = LocalDate.now().getYear();
        int annualLimit = systemSettingsService.requireSettings().getAnnualPaidLeaveLimit();

        for (User user : users) {
            List<LeaveRequest> leaves = leaveRequestRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            for (LeaveRequest leave : leaves) {
                switch (leave.getStatus()) {
                    case LeaveConstants.PENDING -> pending++;
                    case LeaveConstants.APPROVED -> approved++;
                    case LeaveConstants.REJECTED -> rejected++;
                    case LeaveConstants.CANCELLED -> cancelled++;
                    default -> {
                    }
                }
            }
            BigDecimal userPaid = leaveRequestRepository.sumApprovedPaidLeaveDays(user.getId(), year);
            BigDecimal userUnpaid = leaveRequestRepository.sumApprovedUnpaidLeaveDays(user.getId(), year);
            paidUsed = paidUsed.add(userPaid);
            unpaidUsed = unpaidUsed.add(userUnpaid);
            remaining = remaining.add(BigDecimal.valueOf(annualLimit).subtract(userPaid));
        }

        return LeaveSummaryResponseDto.builder()
                .pendingLeaveCount(pending)
                .approvedLeaveCount(approved)
                .rejectedLeaveCount(rejected)
                .cancelledLeaveCount(cancelled)
                .paidLeaveUsed(paidUsed)
                .remainingPaidLeave(remaining)
                .unpaidLeaveUsed(unpaidUsed)
                .build();
    }

    @Transactional(readOnly = true)
    public LeaveSummaryResponseDto getLeaveSummary(LocalDate fromDate, LocalDate toDate) {
        User current = requireHrOrAdmin();
        String targetRole = resolveTargetRole(current);
        List<User> users = usersInRole(targetRole);

        long pending = 0;
        long approved = 0;
        long rejected = 0;
        long cancelled = 0;
        BigDecimal paidUsed = BigDecimal.ZERO;
        BigDecimal unpaidUsed = BigDecimal.ZERO;
        BigDecimal remaining = BigDecimal.ZERO;
        int year = LocalDate.now().getYear();
        int annualLimit = systemSettingsService.requireSettings().getAnnualPaidLeaveLimit();

        for (User user : users) {
            List<LeaveRequest> leaves = leaveRequestRepository.findByUserAndDateRange(user, fromDate, toDate);
            for (LeaveRequest leave : leaves) {
                switch (leave.getStatus()) {
                    case LeaveConstants.PENDING -> pending++;
                    case LeaveConstants.APPROVED -> approved++;
                    case LeaveConstants.REJECTED -> rejected++;
                    case LeaveConstants.CANCELLED -> cancelled++;
                    default -> {
                    }
                }
            }
            BigDecimal userPaid = leaveRequestRepository.sumApprovedPaidLeaveDays(user.getId(), year);
            BigDecimal userUnpaid = leaveRequestRepository.sumApprovedUnpaidLeaveDays(user.getId(), year);
            paidUsed = paidUsed.add(userPaid);
            unpaidUsed = unpaidUsed.add(userUnpaid);
            remaining = remaining.add(BigDecimal.valueOf(annualLimit).subtract(userPaid));
        }

        return LeaveSummaryResponseDto.builder()
                .pendingLeaveCount(pending)
                .approvedLeaveCount(approved)
                .rejectedLeaveCount(rejected)
                .cancelledLeaveCount(cancelled)
                .paidLeaveUsed(paidUsed)
                .remainingPaidLeave(remaining)
                .unpaidLeaveUsed(unpaidUsed)
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardPayrollSummaryDto getPayrollSummary() {
        User current = requireHrOrAdmin();
        String targetRole = resolveTargetRole(current);
        List<Payroll> payrolls = payrollRepository.findByUserRole(targetRole);

        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalPf = BigDecimal.ZERO;
        BigDecimal totalUnpaidDeduction = BigDecimal.ZERO;
        long credited = 0;
        long pending = 0;

        for (Payroll payroll : payrolls) {
            totalPf = totalPf.add(payroll.getPfAmount());
            totalUnpaidDeduction = totalUnpaidDeduction.add(payroll.getUnpaidLeaveDeduction());
            if (PayrollConstants.CREDITED.equals(payroll.getStatus())) {
                totalPaid = totalPaid.add(payroll.getFinalSalary());
                credited++;
            } else {
                pending++;
            }
        }

        return DashboardPayrollSummaryDto.builder()
                .totalSalaryPaid(totalPaid)
                .totalPfDeduction(totalPf)
                .totalUnpaidLeaveDeduction(totalUnpaidDeduction)
                .generatedPayrollCount(payrolls.size())
                .creditedPayrollCount(credited)
                .pendingSalaryCredits(pending)
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardPayrollSummaryDto getPayrollSummary(Integer payrollYear, Integer payrollMonth) {
        User current = requireHrOrAdmin();
        String targetRole = resolveTargetRole(current);
        List<Payroll> payrolls = payrollRepository.findByUserRoleAndPeriod(targetRole, payrollYear, payrollMonth);

        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalPf = BigDecimal.ZERO;
        BigDecimal totalUnpaidDeduction = BigDecimal.ZERO;
        long credited = 0;
        long pending = 0;

        for (Payroll payroll : payrolls) {
            totalPf = totalPf.add(payroll.getPfAmount());
            totalUnpaidDeduction = totalUnpaidDeduction.add(payroll.getUnpaidLeaveDeduction());
            if (PayrollConstants.CREDITED.equals(payroll.getStatus())) {
                totalPaid = totalPaid.add(payroll.getFinalSalary());
                credited++;
            } else {
                pending++;
            }
        }

        return DashboardPayrollSummaryDto.builder()
                .totalSalaryPaid(totalPaid)
                .totalPfDeduction(totalPf)
                .totalUnpaidLeaveDeduction(totalUnpaidDeduction)
                .generatedPayrollCount(payrolls.size())
                .creditedPayrollCount(credited)
                .pendingSalaryCredits(pending)
                .build();
    }

    public String resolveTargetRole(User current) {
        if (RoleConstants.HR.equals(current.getRole())) {
            return RoleConstants.EMPLOYEE;
        }
        if (RoleConstants.ADMIN.equals(current.getRole())) {
            return RoleConstants.HR;
        }
        throw new AccessDeniedException("Access Denied");
    }

    public User requireHrOrAdmin() {
        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.HR.equals(current.getRole()) && !RoleConstants.ADMIN.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }
        return current;
    }

    private List<User> usersInRole(String role) {
        List<User> users = new ArrayList<>();
        users.addAll(userRepository.findByRoleAndStatus(role, StatusConstants.ACTIVE));
        users.addAll(userRepository.findByRoleAndStatus(role, StatusConstants.INACTIVE));
        return users;
    }
}
