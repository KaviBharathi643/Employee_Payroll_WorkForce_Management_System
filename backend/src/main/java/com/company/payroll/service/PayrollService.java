package com.company.payroll.service;

import com.company.payroll.constants.PayrollConstants;
import com.company.payroll.constants.RoleConstants;
import com.company.payroll.constants.StatusConstants;
import com.company.payroll.dto.payroll.GeneratePayrollRequestDto;
import com.company.payroll.dto.payroll.GeneratePayrollResponseDto;
import com.company.payroll.dto.payroll.PayrollResponseDto;
import com.company.payroll.dto.payroll.PayrollSummaryResponseDto;
import com.company.payroll.dto.report.DashboardPayrollSummaryDto;
import com.company.payroll.dto.payroll.SkippedPayrollEmployeeDto;
import com.company.payroll.entity.Payroll;
import com.company.payroll.entity.SystemSettings;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserBankDetails;
import com.company.payroll.entity.UserEmployment;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.exception.BusinessRuleException;
import com.company.payroll.exception.ResourceNotFoundException;
import com.company.payroll.mapper.PayrollMapper;
import com.company.payroll.repository.PayrollRepository;
import com.company.payroll.repository.UserBankDetailsRepository;
import com.company.payroll.repository.UserEmploymentRepository;
import com.company.payroll.repository.UserProfileRepository;
import com.company.payroll.repository.UserRepository;
import com.company.payroll.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserEmploymentRepository userEmploymentRepository;
    private final UserBankDetailsRepository userBankDetailsRepository;
    private final PayrollMapper payrollMapper;
    private final PayrollCalculationService payrollCalculationService;
    private final SystemSettingsService systemSettingsService;
    private final PayslipService payslipService;
    private final DashboardAnalyticsService dashboardAnalyticsService;
    private final CurrentUserService currentUserService;
    private final NotificationHelperService notificationHelperService;
    private final EmailService emailService;

    @Transactional
    public synchronized GeneratePayrollResponseDto generatePayroll(GeneratePayrollRequestDto request) {
        User generator = currentUserService.getCurrentUser();
        String targetRole = resolveTargetRoleForGeneration(generator);

        BigDecimal bonus = request.getBonusAmount() != null ? request.getBonusAmount() : BigDecimal.ZERO;
        List<User> activeUsers = userRepository.findByRoleAndStatus(targetRole, StatusConstants.ACTIVE);

        int processed = 0;
        int skipped = 0;
        List<SkippedPayrollEmployeeDto> skippedList = new ArrayList<>();

        for (User user : activeUsers) {
            SkippedPayrollEmployeeDto skip = generateSinglePayroll(
                    user, request.getPayrollYear(), request.getPayrollMonth(), bonus, generator);
            if (skip == null) {
                processed++;
            } else {
                skipped++;
                skippedList.add(skip);
            }
        }

        return GeneratePayrollResponseDto.builder()
                .processedCount(processed)
                .skippedCount(skipped)
                .skippedEmployees(skippedList)
                .build();
    }

    @Transactional
    public PayrollResponseDto creditSalary(Long payrollId) {
        User actor = currentUserService.getCurrentUser();
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll Not Found"));

        validateCreditAccess(actor, payroll.getUser().getRole());

        if (PayrollConstants.CREDITED.equals(payroll.getStatus())) {
            throw new BusinessRuleException("Salary Already Credited");
        }

        payroll.setStatus(PayrollConstants.CREDITED);
        payroll.setCreditedDate(LocalDateTime.now());
        payroll.setCreditedBy(actor);
        payroll = payrollRepository.save(payroll);

        notificationHelperService.notifySalaryCredited(payroll.getUser(), payroll.getId());
        emailService.sendSalaryCreditedEmail(payroll.getUser().getEmail());

        return payrollMapper.toResponse(payroll);
    }

    @Transactional(readOnly = true)
    public List<PayrollResponseDto> listPayrolls() {
        User current = currentUserService.getCurrentUser();
        if (RoleConstants.EMPLOYEE.equals(current.getRole())) {
            return payrollRepository.findByUserIdOrderByPayrollYearDescPayrollMonthDesc(current.getId())
                    .stream().map(payrollMapper::toResponse).toList();
        }
        if (RoleConstants.HR.equals(current.getRole())) {
            return payrollRepository.findByUserRole(RoleConstants.EMPLOYEE).stream()
                    .map(payrollMapper::toResponse).toList();
        }
        if (RoleConstants.ADMIN.equals(current.getRole())) {
            return payrollRepository.findByUserRole(RoleConstants.HR).stream()
                    .map(payrollMapper::toResponse).toList();
        }
        throw new AccessDeniedException("Access Denied");
    }

    @Transactional(readOnly = true)
    public PayrollResponseDto getPayrollDetails(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll Not Found"));
        validatePayrollViewAccess(currentUserService.getCurrentUser(), payroll);
        return payrollMapper.toResponse(payroll);
    }

    @Transactional(readOnly = true)
    public List<PayrollResponseDto> getPayrollHistory() {
        User current = currentUserService.getCurrentUser();
        return payrollRepository.findByUserIdOrderByPayrollYearDescPayrollMonthDesc(current.getId())
                .stream().map(payrollMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PayrollResponseDto> getPayrollReport(Integer payrollYear, Integer payrollMonth) {
        User current = currentUserService.getCurrentUser();
        String role = RoleConstants.HR.equals(current.getRole()) ? RoleConstants.EMPLOYEE : RoleConstants.HR;
        if (!RoleConstants.HR.equals(current.getRole()) && !RoleConstants.ADMIN.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }
        return payrollRepository.findByUserRoleAndPeriod(role, payrollYear, payrollMonth).stream()
                .map(payrollMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PayrollSummaryResponseDto getPayrollSummary() {
        DashboardPayrollSummaryDto summary = dashboardAnalyticsService.getPayrollSummary();
        return PayrollSummaryResponseDto.builder()
                .totalSalaryPaid(summary.getTotalSalaryPaid())
                .totalEmployeesPaid(summary.getCreditedPayrollCount())
                .totalUnpaidLeaveDeductions(summary.getTotalUnpaidLeaveDeduction())
                .pendingSalaryCredits(summary.getPendingSalaryCredits())
                .build();
    }

    @Transactional(readOnly = true)
    public void sendPayrollReminderIfNeeded() {
        SystemSettings settings = systemSettingsService.requireSettings();
        LocalDateTime now = LocalDateTime.now();
        if (now.getDayOfMonth() != settings.getSalaryCreditDay()) {
            return;
        }

        int prevMonth = now.getMonthValue() == 1 ? 12 : now.getMonthValue() - 1;
        int prevYear = now.getMonthValue() == 1 ? now.getYear() - 1 : now.getYear();

        long employeePayrolls = payrollRepository.countByUserRoleAndPeriod(
                RoleConstants.EMPLOYEE, prevYear, prevMonth);
        long activeEmployees = userRepository.findByRoleAndStatus(RoleConstants.EMPLOYEE, StatusConstants.ACTIVE).size();
        if (employeePayrolls == 0 && activeEmployees > 0) {
            notifyPayrollReminder(RoleConstants.HR, prevYear, prevMonth);
        }

        long hrPayrolls = payrollRepository.countByUserRoleAndPeriod(RoleConstants.HR, prevYear, prevMonth);
        long activeHr = userRepository.findByRoleAndStatus(RoleConstants.HR, StatusConstants.ACTIVE).size();
        if (hrPayrolls == 0 && activeHr > 0) {
            notifyPayrollReminder(RoleConstants.ADMIN, prevYear, prevMonth);
        }
    }

    private SkippedPayrollEmployeeDto generateSinglePayroll(
            User user,
            Integer payrollYear,
            Integer payrollMonth,
            BigDecimal bonusAmount,
            User generatedBy) {

        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        String code = profile != null ? profile.getEmployeeCode() : user.getEmail();
        String name = profile != null ? profile.getFullName() : user.getEmail();

        if (payrollRepository.findByUserIdAndPayrollYearAndPayrollMonth(user.getId(), payrollYear, payrollMonth)
                .isPresent()) {
            return skipped(code, name, "Payroll Already Generated");
        }

        UserEmployment employment = userEmploymentRepository.findByUser(user).orElse(null);
        if (employment == null || !StatusConstants.ACTIVE.equals(employment.getEmploymentStatus())) {
            return skipped(code, name, "Employee Not Active");
        }

        UserBankDetails bank = userBankDetailsRepository.findByUser(user).orElse(null);
        if (bank == null) {
            return skipped(code, name, "Bank Details Missing");
        }

        if (profile == null) {
            return skipped(code, name, "Profile Not Found");
        }

        SystemSettings settings = systemSettingsService.requireSettings();
        BigDecimal basicSalary = employment.getBasicSalary();
        BigDecimal pfPercentage = payrollCalculationService.resolvePfPercentage(settings);
        BigDecimal pfAmount = payrollCalculationService.calculatePfAmount(basicSalary, pfPercentage);
        BigDecimal unpaidLeaveCount = payrollCalculationService.calculateUnpaidLeaveCount(
                user.getId(), payrollYear, payrollMonth);
        BigDecimal unpaidDeduction = payrollCalculationService.calculateUnpaidLeaveDeduction(
                basicSalary, unpaidLeaveCount);
        BigDecimal finalSalary = payrollCalculationService.calculateFinalSalary(
                basicSalary, bonusAmount, pfAmount, unpaidDeduction);

        Payroll payroll = Payroll.builder()
                .user(user)
                .payrollYear(payrollYear)
                .payrollMonth(payrollMonth)
                .employeeCode(profile.getEmployeeCode())
                .employeeName(profile.getFullName())
                .department(employment.getDepartment())
                .designation(employment.getDesignation())
                .basicSalary(basicSalary)
                .bonus(bonusAmount)
                .pfPercentage(pfPercentage)
                .pfAmount(pfAmount)
                .unpaidLeaveCount(unpaidLeaveCount)
                .unpaidLeaveDeduction(unpaidDeduction)
                .finalSalary(finalSalary)
                .generatedDate(LocalDateTime.now())
                .generatedBy(generatedBy)
                .status(PayrollConstants.GENERATED)
                .build();
        payroll = payrollRepository.save(payroll);

        payslipService.generatePayslip(payroll.getId());
        notificationHelperService.notifyPayrollGenerated(user, payroll.getId());
        emailService.sendPayrollGeneratedEmail(user.getEmail());

        return null;
    }

    private void notifyPayrollReminder(String role, int year, int month) {
        List<User> recipients = userRepository.findByRoleAndStatus(role, StatusConstants.ACTIVE);
        for (User recipient : recipients) {
            notificationHelperService.notifyPayrollReminder(recipient, year, month);
            emailService.sendPayrollReminderEmail(recipient.getEmail(), year, month);
        }
    }

    private SkippedPayrollEmployeeDto skipped(String code, String name, String reason) {
        return SkippedPayrollEmployeeDto.builder()
                .employeeCode(code)
                .employeeName(name)
                .reason(reason)
                .build();
    }

    private String resolveTargetRoleForGeneration(User generator) {
        if (RoleConstants.HR.equals(generator.getRole())) {
            return RoleConstants.EMPLOYEE;
        }
        if (RoleConstants.ADMIN.equals(generator.getRole())) {
            return RoleConstants.HR;
        }
        throw new AccessDeniedException("Access Denied");
    }

    private void validateCreditAccess(User actor, String payrollUserRole) {
        if (RoleConstants.EMPLOYEE.equals(payrollUserRole) && RoleConstants.HR.equals(actor.getRole())) {
            return;
        }
        if (RoleConstants.HR.equals(payrollUserRole) && RoleConstants.ADMIN.equals(actor.getRole())) {
            return;
        }
        throw new AccessDeniedException("Unauthorized Access");
    }

    private void validatePayrollViewAccess(User viewer, Payroll payroll) {
        if (payroll.getUser().getId().equals(viewer.getId())) {
            return;
        }
        if (RoleConstants.HR.equals(viewer.getRole()) && RoleConstants.EMPLOYEE.equals(payroll.getUser().getRole())) {
            return;
        }
        if (RoleConstants.ADMIN.equals(viewer.getRole()) && RoleConstants.HR.equals(payroll.getUser().getRole())) {
            return;
        }
        throw new AccessDeniedException("Unauthorized Access");
    }
}
