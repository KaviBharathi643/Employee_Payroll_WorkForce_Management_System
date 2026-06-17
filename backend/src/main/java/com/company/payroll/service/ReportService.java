package com.company.payroll.service;

import com.company.payroll.constants.AttendanceConstants;
import com.company.payroll.constants.LeaveConstants;
import com.company.payroll.constants.ReportConstants;
import com.company.payroll.constants.StatusConstants;
import com.company.payroll.dto.attendance.AttendanceSummaryResponseDto;
import com.company.payroll.dto.leave.LeaveResponseDto;
import com.company.payroll.dto.report.DashboardPayrollSummaryDto;
import com.company.payroll.dto.report.LeaveSummaryResponseDto;
import com.company.payroll.dto.report.ReportPdfData;
import com.company.payroll.dto.report.UserCountSummaryDto;
import com.company.payroll.entity.Attendance;
import com.company.payroll.entity.Payroll;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserEmployment;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.exception.BusinessRuleException;
import com.company.payroll.repository.AttendanceRepository;
import com.company.payroll.repository.PayrollRepository;
import com.company.payroll.repository.ReportSpecifications;
import com.company.payroll.repository.UserEmploymentRepository;
import com.company.payroll.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final DashboardAnalyticsService dashboardAnalyticsService;
    private final PdfService pdfService;
    private final SystemSettingsService systemSettingsService;
    private final UserProfileRepository userProfileRepository;
    private final UserEmploymentRepository userEmploymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceStatusService attendanceStatusService;
    private final LeaveService leaveService;
    private final PayrollRepository payrollRepository;

    @Transactional(readOnly = true)
    public byte[] generateEmployeeReport(
            String employmentStatus,
            String department,
            String designation,
            String employeeCode) {
        User current = dashboardAnalyticsService.requireHrOrAdmin();
        String targetRole = dashboardAnalyticsService.resolveTargetRole(current);
        List<UserProfile> profiles = fetchProfiles(targetRole, employmentStatus, department, designation, employeeCode);

        List<List<String>> rows = new ArrayList<>();
        long active = 0;
        long inactive = 0;

        for (UserProfile profile : profiles) {
            User user = profile.getUser();
            UserEmployment employment = userEmploymentRepository.findByUser(user).orElse(null);
            if (StatusConstants.ACTIVE.equals(user.getStatus())) {
                active++;
            } else {
                inactive++;
            }
            rows.add(List.of(
                    profile.getEmployeeCode(),
                    profile.getFullName(),
                    user.getEmail(),
                    profile.getPhoneNumber(),
                    employment != null ? employment.getDepartment() : "",
                    employment != null ? employment.getDesignation() : "",
                    employment != null ? employment.getJoiningDate().format(DATE_FORMAT) : "",
                    employment != null ? employment.getEmploymentType() : "",
                    employment != null ? employment.getEmploymentStatus() : user.getStatus()
            ));
        }

        UserCountSummaryDto summary = UserCountSummaryDto.builder()
                .totalUsers(profiles.size())
                .activeUsers(active)
                .inactiveUsers(inactive)
                .build();

        ReportPdfData pdfData = ReportPdfData.builder()
                .reportType(ReportConstants.EMPLOYEE_REPORT)
                .generatedBy(resolveGeneratedBy(current))
                .headers(List.of(
                        "Code", "Name", "Email", "Phone", "Department", "Designation",
                        "Joining Date", "Employment Type", "Status"))
                .rows(rows)
                .summary(userCountSummaryMap(summary))
                .build();

        return pdfService.createReportPdf(systemSettingsService.requireSettings(), pdfData);
    }

    @Transactional(readOnly = true)
    public byte[] generateAttendanceReport(
            LocalDate fromDate,
            LocalDate toDate,
            String department,
            String designation,
            String employeeCode) {
        User current = dashboardAnalyticsService.requireHrOrAdmin();
        validateDateRange(fromDate, toDate);
        String targetRole = dashboardAnalyticsService.resolveTargetRole(current);

        LocalDate from = fromDate != null ? fromDate : YearMonth.now().atDay(1);
        LocalDate to = toDate != null ? toDate : LocalDate.now();

        Set<Long> allowedUserIds = resolveFilteredUserIds(
                targetRole, ReportConstants.EMPLOYMENT_STATUS_ALL, department, designation, employeeCode);

        List<Attendance> records = attendanceRepository.findByUserRoleAndAttendanceDateBetween(targetRole, from, to);
        List<List<String>> rows = new ArrayList<>();

        for (Attendance attendance : records) {
            User user = attendance.getUser();
            if (!allowedUserIds.isEmpty() && !allowedUserIds.contains(user.getId())) {
                continue;
            }
            UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
            String status = attendanceStatusService.calculateAttendanceStatus(user.getId(), attendance.getAttendanceDate());
            rows.add(List.of(
                    profile != null ? profile.getEmployeeCode() : "",
                    profile != null ? profile.getFullName() : "",
                    attendance.getAttendanceDate().format(DATE_FORMAT),
                    attendance.getCheckInTime() != null ? attendance.getCheckInTime().format(TIME_FORMAT) : "",
                    attendance.getCheckOutTime() != null ? attendance.getCheckOutTime().format(TIME_FORMAT) : "",
                    status
            ));
        }

        AttendanceSummaryResponseDto summary = hasWorkforceFilters(department, designation, employeeCode)
                ? buildFilteredAttendanceSummary(allowedUserIds, from, to)
                : dashboardAnalyticsService.getAttendanceSummary(from, to);

        ReportPdfData pdfData = ReportPdfData.builder()
                .reportType(ReportConstants.ATTENDANCE_REPORT)
                .generatedBy(resolveGeneratedBy(current))
                .dateRange(from.format(DATE_FORMAT) + " to " + to.format(DATE_FORMAT))
                .headers(List.of("Code", "Name", "Date", "Check In", "Check Out", "Status"))
                .rows(rows)
                .summary(attendanceSummaryMap(summary))
                .build();

        return pdfService.createReportPdf(systemSettingsService.requireSettings(), pdfData);
    }

    @Transactional(readOnly = true)
    public byte[] generateLeaveReport(
            LocalDate fromDate,
            LocalDate toDate,
            String department,
            String designation,
            String employeeCode,
            String leaveType,
            String leaveStatus) {
        User current = dashboardAnalyticsService.requireHrOrAdmin();
        validateDateRange(fromDate, toDate);

        LocalDate from = fromDate != null ? fromDate : YearMonth.now().atDay(1);
        LocalDate to = toDate != null ? toDate : LocalDate.now();

        List<LeaveResponseDto> leaves = leaveService.getLeaveReportData(from, to, null).stream()
                .filter(leave -> matchesOptional(leave.getEmployeeCode(), employeeCode))
                .filter(leave -> leaveType == null || leaveType.isBlank()
                        || leaveType.equalsIgnoreCase(leave.getLeaveType()))
                .filter(leave -> leaveStatus == null || leaveStatus.isBlank()
                        || leaveStatus.equalsIgnoreCase(leave.getStatus()))
                .filter(leave -> matchesEmploymentFilters(leave.getUserId(), department, designation))
                .toList();

        List<List<String>> rows = new ArrayList<>(leaves.stream()
                .map(leave -> List.of(
                        nullSafe(leave.getEmployeeCode()),
                        nullSafe(leave.getEmployeeName()),
                        nullSafe(leave.getLeaveType()),
                        nullSafe(leave.getDurationType()),
                        leave.getLeaveDays() != null ? leave.getLeaveDays().toPlainString() : "",
                        leave.getStartDate() != null ? leave.getStartDate().format(DATE_FORMAT) : "",
                        leave.getEndDate() != null ? leave.getEndDate().format(DATE_FORMAT) : "",
                        nullSafe(leave.getStatus()),
                        nullSafe(leave.getApprovedByName())
                ))
                .toList());

        LeaveSummaryResponseDto balanceSummary = dashboardAnalyticsService.getLeaveSummary(from, to);
        LeaveSummaryResponseDto summary = LeaveSummaryResponseDto.builder()
                .pendingLeaveCount(leaves.stream().filter(l -> LeaveConstants.PENDING.equals(l.getStatus())).count())
                .approvedLeaveCount(leaves.stream().filter(l -> LeaveConstants.APPROVED.equals(l.getStatus())).count())
                .rejectedLeaveCount(leaves.stream().filter(l -> LeaveConstants.REJECTED.equals(l.getStatus())).count())
                .cancelledLeaveCount(leaves.stream().filter(l -> LeaveConstants.CANCELLED.equals(l.getStatus())).count())
                .paidLeaveUsed(balanceSummary.getPaidLeaveUsed())
                .remainingPaidLeave(balanceSummary.getRemainingPaidLeave())
                .unpaidLeaveUsed(balanceSummary.getUnpaidLeaveUsed())
                .build();

        ReportPdfData pdfData = ReportPdfData.builder()
                .reportType(ReportConstants.LEAVE_REPORT)
                .generatedBy(resolveGeneratedBy(current))
                .dateRange(from.format(DATE_FORMAT) + " to " + to.format(DATE_FORMAT))
                .headers(List.of(
                        "Code", "Name", "Leave Type", "Duration", "Days", "Start", "End", "Status", "Approved By"))
                .rows(rows)
                .summary(leaveSummaryMap(summary))
                .build();

        return pdfService.createReportPdf(systemSettingsService.requireSettings(), pdfData);
    }

    @Transactional(readOnly = true)
    public byte[] generatePayrollReport(Integer payrollYear, Integer payrollMonth) {
        if (payrollYear == null || payrollMonth == null) {
            throw new BusinessRuleException("Payroll Year And Month Required");
        }

        User current = dashboardAnalyticsService.requireHrOrAdmin();
        String targetRole = dashboardAnalyticsService.resolveTargetRole(current);
        List<Payroll> payrolls = payrollRepository.findByUserRoleAndPeriod(targetRole, payrollYear, payrollMonth);

        List<List<String>> rows = payrolls.stream()
                .map(payroll -> List.of(
                        payroll.getEmployeeCode(),
                        payroll.getEmployeeName(),
                        payroll.getDepartment(),
                        payroll.getDesignation(),
                        payroll.getBasicSalary().toPlainString(),
                        payroll.getBonus().toPlainString(),
                        payroll.getPfAmount().toPlainString(),
                        payroll.getUnpaidLeaveCount().toPlainString(),
                        payroll.getUnpaidLeaveDeduction().toPlainString(),
                        payroll.getFinalSalary().toPlainString(),
                        payroll.getStatus()
                ))
                .toList();

        DashboardPayrollSummaryDto summary = dashboardAnalyticsService.getPayrollSummary(payrollYear, payrollMonth);

        ReportPdfData pdfData = ReportPdfData.builder()
                .reportType(ReportConstants.PAYROLL_REPORT)
                .generatedBy(resolveGeneratedBy(current))
                .dateRange("Month " + payrollMonth + " / " + payrollYear)
                .headers(List.of(
                        "Code", "Name", "Department", "Designation", "Basic", "Bonus", "PF",
                        "Unpaid Leaves", "Unpaid Deduction", "Final Salary", "Status"))
                .rows(rows)
                .summary(payrollSummaryMap(summary))
                .build();

        return pdfService.createReportPdf(systemSettingsService.requireSettings(), pdfData);
    }

    public String buildReportFileName(String prefix) {
        return prefix + "-" + LocalDate.now().format(DATE_FORMAT) + ".pdf";
    }

    private List<UserProfile> fetchProfiles(
            String role,
            String employmentStatus,
            String department,
            String designation,
            String employeeCode) {
        Specification<UserProfile> spec = ReportSpecifications.workforceReport(
                role, employmentStatus, department, designation, employeeCode);
        return userProfileRepository.findAll(spec);
    }

    private Set<Long> resolveFilteredUserIds(
            String role,
            String employmentStatus,
            String department,
            String designation,
            String employeeCode) {
        if (!hasWorkforceFilters(department, designation, employeeCode)) {
            return Set.of();
        }
        return fetchProfiles(role, employmentStatus, department, designation, employeeCode).stream()
                .map(profile -> profile.getUser().getId())
                .collect(Collectors.toSet());
    }

    private AttendanceSummaryResponseDto buildFilteredAttendanceSummary(
            Set<Long> userIds, LocalDate fromDate, LocalDate toDate) {
        long present = 0;
        long absent = 0;
        long leave = 0;
        long halfDay = 0;

        for (Long userId : userIds) {
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
        }

        return AttendanceSummaryResponseDto.builder()
                .presentCount(present)
                .absentCount(absent)
                .leaveCount(leave)
                .halfDayLeaveCount(halfDay)
                .missingCheckoutCount(0)
                .build();
    }

    private boolean matchesEmploymentFilters(Long userId, String department, String designation) {
        if (!hasWorkforceFilters(department, designation, null)) {
            return true;
        }
        UserEmployment employment = userEmploymentRepository.findByUserId(userId).orElse(null);
        if (employment == null) {
            return false;
        }
        if (department != null && !department.isBlank() && !department.equals(employment.getDepartment())) {
            return false;
        }
        return designation == null || designation.isBlank() || designation.equals(employment.getDesignation());
    }

    private boolean hasWorkforceFilters(String department, String designation, String employeeCode) {
        return (department != null && !department.isBlank())
                || (designation != null && !designation.isBlank())
                || (employeeCode != null && !employeeCode.isBlank());
    }

    private boolean matchesOptional(String value, String filter) {
        return filter == null || filter.isBlank() || filter.equalsIgnoreCase(value);
    }

    private String resolveGeneratedBy(User user) {
        return userProfileRepository.findByUser(user)
                .map(UserProfile::getFullName)
                .orElse(user.getEmail());
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessRuleException("Invalid Date Range");
        }
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    private Map<String, String> userCountSummaryMap(UserCountSummaryDto summary) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Total Users", String.valueOf(summary.getTotalUsers()));
        map.put("Active Users", String.valueOf(summary.getActiveUsers()));
        map.put("Inactive Users", String.valueOf(summary.getInactiveUsers()));
        return map;
    }

    private Map<String, String> attendanceSummaryMap(AttendanceSummaryResponseDto summary) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Present Count", String.valueOf(summary.getPresentCount()));
        map.put("Absent Count", String.valueOf(summary.getAbsentCount()));
        map.put("Leave Count", String.valueOf(summary.getLeaveCount()));
        map.put("Half Day Leave Count", String.valueOf(summary.getHalfDayLeaveCount()));
        return map;
    }

    private Map<String, String> leaveSummaryMap(LeaveSummaryResponseDto summary) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Pending Leave Count", String.valueOf(summary.getPendingLeaveCount()));
        map.put("Approved Leave Count", String.valueOf(summary.getApprovedLeaveCount()));
        map.put("Rejected Leave Count", String.valueOf(summary.getRejectedLeaveCount()));
        map.put("Cancelled Leave Count", String.valueOf(summary.getCancelledLeaveCount()));
        map.put("Paid Leave Used", summary.getPaidLeaveUsed().toPlainString());
        map.put("Remaining Paid Leave", summary.getRemainingPaidLeave().toPlainString());
        map.put("Unpaid Leave Used", summary.getUnpaidLeaveUsed().toPlainString());
        return map;
    }

    private Map<String, String> payrollSummaryMap(DashboardPayrollSummaryDto summary) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Total Salary Paid", summary.getTotalSalaryPaid().toPlainString());
        map.put("Total PF Deduction", summary.getTotalPfDeduction().toPlainString());
        map.put("Total Unpaid Leave Deduction", summary.getTotalUnpaidLeaveDeduction().toPlainString());
        map.put("Generated Payroll Count", String.valueOf(summary.getGeneratedPayrollCount()));
        map.put("Credited Payroll Count", String.valueOf(summary.getCreditedPayrollCount()));
        map.put("Pending Salary Credits", String.valueOf(summary.getPendingSalaryCredits()));
        return map;
    }
}
