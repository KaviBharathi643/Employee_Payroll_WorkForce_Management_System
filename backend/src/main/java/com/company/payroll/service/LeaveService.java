package com.company.payroll.service;

import com.company.payroll.constants.LeaveConstants;
import com.company.payroll.constants.RoleConstants;
import com.company.payroll.constants.StatusConstants;
import com.company.payroll.dto.leave.ApplyLeaveRequestDto;
import com.company.payroll.dto.leave.LeaveBalanceResponseDto;
import com.company.payroll.dto.leave.LeaveResponseDto;
import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.SystemSettings;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.exception.BusinessRuleException;
import com.company.payroll.exception.ResourceNotFoundException;
import com.company.payroll.exception.ValidationException;
import com.company.payroll.mapper.LeaveMapper;
import com.company.payroll.repository.LeaveRequestRepository;
import com.company.payroll.repository.UserProfileRepository;
import com.company.payroll.repository.UserRepository;
import com.company.payroll.security.CurrentUserService;
import com.company.payroll.util.LeaveCalculationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final LeaveMapper leaveMapper;
    private final CurrentUserService currentUserService;
    private final SystemSettingsService systemSettingsService;
    private final NotificationHelperService notificationHelperService;
    private final EmailService emailService;
    private final AttendanceStatusRecalculationService attendanceStatusRecalculationService;

    @Transactional
    public LeaveResponseDto applyLeave(ApplyLeaveRequestDto request) {
        User applicant = currentUserService.getCurrentUser();
        if (!RoleConstants.EMPLOYEE.equals(applicant.getRole()) && !RoleConstants.HR.equals(applicant.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }

        if (request.getReason() == null || request.getReason().isBlank()) {
            throw new ValidationException("Leave Reason Required");
        }

        LocalDate today = LocalDate.now();
        if (request.getStartDate().isBefore(today)) {
            throw new ValidationException("Past Leave Not Allowed");
        }

        LeaveCalculationUtil.validateDateRange(request.getStartDate(), request.getEndDate(), request.getDurationType());

        if (leaveRequestRepository.existsApprovedLeaveBetweenDates(
                applicant.getId(), request.getStartDate(), request.getEndDate())) {
            throw new BusinessRuleException("Leave Already Exists For Selected Date");
        }

        if (leaveRequestRepository.existsApprovedLeaveOnDate(applicant.getId(), today)) {
            throw new BusinessRuleException("You Are Already In Leave On That Date");
        }

        BigDecimal leaveDays = LeaveCalculationUtil.calculateLeaveDays(
                request.getStartDate(), request.getEndDate(), request.getDurationType());

        LeaveRequest leave = LeaveRequest.builder()
                .user(applicant)
                .leaveType(request.getLeaveType())
                .durationType(request.getDurationType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .leaveDays(leaveDays)
                .reason(request.getReason())
                .status(LeaveConstants.PENDING)
                .build();
        leave = leaveRequestRepository.save(leave);

        String applicantName = userProfileRepository.findByUser(applicant)
                .map(UserProfile::getFullName)
                .orElse(applicant.getEmail());
        notifyApproversOnApply(applicant, leave.getId(), applicantName);

        return leaveMapper.toResponse(leave);
    }

    @Transactional
    public LeaveResponseDto approveLeave(Long leaveId) {
        User approver = currentUserService.getCurrentUser();
        LeaveRequest leave = getLeaveOrThrow(leaveId);
        validateApprover(approver, leave.getUser());

        if (!LeaveConstants.PENDING.equals(leave.getStatus())) {
            throw new BusinessRuleException("Only pending leave can be approved");
        }

        leave.setStatus(LeaveConstants.APPROVED);
        leave.setApprovedBy(approver);
        leave = leaveRequestRepository.save(leave);

        attendanceStatusRecalculationService.recalculateForLeaveChange(
                leave.getUser(), leave.getStartDate(), leave.getEndDate());

        notificationHelperService.notifyLeaveApproved(leave.getUser(), leave.getId());
        emailService.sendLeaveApprovedEmail(leave.getUser().getEmail());

        return leaveMapper.toResponse(leave);
    }

    @Transactional
    public LeaveResponseDto rejectLeave(Long leaveId) {
        User approver = currentUserService.getCurrentUser();
        LeaveRequest leave = getLeaveOrThrow(leaveId);
        validateApprover(approver, leave.getUser());

        if (!LeaveConstants.PENDING.equals(leave.getStatus())) {
            throw new BusinessRuleException("Only pending leave can be rejected");
        }

        leave.setStatus(LeaveConstants.REJECTED);
        leave.setApprovedBy(approver);
        leave = leaveRequestRepository.save(leave);

        notificationHelperService.notifyLeaveRejected(leave.getUser(), leave.getId());
        emailService.sendLeaveRejectedEmail(leave.getUser().getEmail());

        return leaveMapper.toResponse(leave);
    }

    @Transactional
    public LeaveResponseDto cancelLeave(Long leaveId) {
        User current = currentUserService.getCurrentUser();
        LeaveRequest leave = getLeaveOrThrow(leaveId);

        if (!leave.getUser().getId().equals(current.getId())) {
            throw new AccessDeniedException("Unauthorized Cancellation");
        }

        leave.setStatus(LeaveConstants.CANCELLED);
        leave.setCancelledAt(LocalDateTime.now());
        leave = leaveRequestRepository.save(leave);

        notificationHelperService.notifyLeaveCancelled(
                leave.getUser(), leave.getId(), "Your leave request has been cancelled.");
        emailService.sendLeaveCancelledEmail(leave.getUser().getEmail());

        if (leave.getApprovedBy() != null) {
            notificationHelperService.notifyLeaveCancelled(
                    leave.getApprovedBy(), leave.getId(), "A leave request you processed has been cancelled.");
            emailService.sendLeaveCancelledEmail(leave.getApprovedBy().getEmail());
        }

        return leaveMapper.toResponse(leave);
    }

    @Transactional
    public LeaveResponseDto convertToUnpaidLeave(Long leaveId) {
        User actor = currentUserService.getCurrentUser();
        LeaveRequest leave = getLeaveOrThrow(leaveId);
        validateConvertPermission(actor, leave.getUser());

        if (!LeaveConstants.APPROVED.equals(leave.getStatus())) {
            throw new BusinessRuleException("Only approved leave can be converted to unpaid");
        }

        leave.setLeaveType(LeaveConstants.UNPAID);
        leave = leaveRequestRepository.save(leave);

        notificationHelperService.notifyLeaveConvertedToUnpaid(leave.getUser(), leave.getId());
        emailService.sendLeaveConvertedEmail(leave.getUser().getEmail());

        return leaveMapper.toResponse(leave);
    }

    @Transactional(readOnly = true)
    public List<LeaveResponseDto> getMyLeaveHistory() {
        User current = currentUserService.getCurrentUser();
        return leaveRequestRepository.findByUserIdOrderByCreatedAtDesc(current.getId()).stream()
                .map(leaveMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LeaveBalanceResponseDto getLeaveBalance() {
        User current = currentUserService.getCurrentUser();
        return buildLeaveBalance(current.getId());
    }

    @Transactional(readOnly = true)
    public List<LeaveResponseDto> getEmployeeLeaveHistory(Long employeeId) {
        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.HR.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }
        User employee = userRepository.findByIdAndRole(employeeId, RoleConstants.EMPLOYEE)
                .orElseThrow(() -> new ResourceNotFoundException("Employee Not Found"));
        return leaveRequestRepository.findByUserIdOrderByCreatedAtDesc(employee.getId()).stream()
                .map(leaveMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveResponseDto> getHrLeaveHistory(Long hrId) {
        User current = currentUserService.getCurrentUser();
        if (!RoleConstants.ADMIN.equals(current.getRole())) {
            throw new AccessDeniedException("Access Denied");
        }
        User hr = userRepository.findByIdAndRole(hrId, RoleConstants.HR)
                .orElseThrow(() -> new ResourceNotFoundException("HR Not Found"));
        return leaveRequestRepository.findByUserIdOrderByCreatedAtDesc(hr.getId()).stream()
                .map(leaveMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveResponseDto> getLeaveReportData(LocalDate fromDate, LocalDate toDate, Long userId) {
        User current = currentUserService.getCurrentUser();
        if (RoleConstants.HR.equals(current.getRole())) {
            if (userId != null) {
                User employee = userRepository.findByIdAndRole(userId, RoleConstants.EMPLOYEE)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee Not Found"));
                return leaveRequestRepository.findByUserAndDateRange(employee, fromDate, toDate).stream()
                        .map(leaveMapper::toResponse)
                        .toList();
            }
            return leaveRequestRepository.findByUserRoleAndDateRange(RoleConstants.EMPLOYEE, fromDate, toDate).stream()
                    .map(leaveMapper::toResponse)
                    .toList();
        }
        if (RoleConstants.ADMIN.equals(current.getRole())) {
            if (userId != null) {
                User hr = userRepository.findByIdAndRole(userId, RoleConstants.HR)
                        .orElseThrow(() -> new ResourceNotFoundException("HR Not Found"));
                return leaveRequestRepository.findByUserAndDateRange(hr, fromDate, toDate).stream()
                        .map(leaveMapper::toResponse)
                        .toList();
            }
            return leaveRequestRepository.findByUserRoleAndDateRange(RoleConstants.HR, fromDate, toDate).stream()
                    .map(leaveMapper::toResponse)
                    .toList();
        }
        throw new AccessDeniedException("Access Denied");
    }

    private LeaveBalanceResponseDto buildLeaveBalance(Long userId) {
        SystemSettings settings = systemSettingsService.requireSettings();
        int year = LocalDate.now().getYear();
        BigDecimal paidUsed = leaveRequestRepository.sumApprovedPaidLeaveDays(userId, year);
        BigDecimal unpaidUsed = leaveRequestRepository.sumApprovedUnpaidLeaveDays(userId, year);
        BigDecimal limit = BigDecimal.valueOf(settings.getAnnualPaidLeaveLimit());
        BigDecimal remaining = limit.subtract(paidUsed);

        return LeaveBalanceResponseDto.builder()
                .paidLeaveUsed(paidUsed)
                .remainingPaidLeave(remaining)
                .unpaidLeaveUsed(unpaidUsed)
                .annualPaidLeaveLimit(settings.getAnnualPaidLeaveLimit())
                .build();
    }

    private void notifyApproversOnApply(User applicant, Long leaveId, String applicantName) {
        String approverRole = RoleConstants.EMPLOYEE.equals(applicant.getRole())
                ? RoleConstants.HR
                : RoleConstants.ADMIN;
        List<User> approvers = userRepository.findByRoleAndStatus(approverRole, StatusConstants.ACTIVE);
        for (User approver : approvers) {
            notificationHelperService.notifyLeaveApplied(approver, leaveId, applicantName);
            emailService.sendLeaveAppliedEmail(approver.getEmail(), applicantName);
        }
    }

    private void validateApprover(User approver, User applicant) {
        if (RoleConstants.EMPLOYEE.equals(applicant.getRole()) && !RoleConstants.HR.equals(approver.getRole())) {
            throw new AccessDeniedException("Unauthorized Approval");
        }
        if (RoleConstants.HR.equals(applicant.getRole()) && !RoleConstants.ADMIN.equals(approver.getRole())) {
            throw new AccessDeniedException("Unauthorized Approval");
        }
        if (RoleConstants.ADMIN.equals(applicant.getRole())) {
            throw new AccessDeniedException("Unauthorized Approval");
        }
    }

    private void validateConvertPermission(User actor, User applicant) {
        if (RoleConstants.EMPLOYEE.equals(applicant.getRole())
                && (RoleConstants.HR.equals(actor.getRole()) || RoleConstants.ADMIN.equals(actor.getRole()))) {
            return;
        }
        if (RoleConstants.HR.equals(applicant.getRole()) && RoleConstants.ADMIN.equals(actor.getRole())) {
            return;
        }
        throw new AccessDeniedException("Access Denied");
    }

    private LeaveRequest getLeaveOrThrow(Long leaveId) {
        return leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Not Found"));
    }
}
