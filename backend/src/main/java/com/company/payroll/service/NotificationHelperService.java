package com.company.payroll.service;

import com.company.payroll.constants.NotificationConstants;
import com.company.payroll.entity.Notification;
import com.company.payroll.entity.User;
import com.company.payroll.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationHelperService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void notifyAccountCreated(User user) {
        Notification notification = Notification.builder()
                .user(user)
                .title("Account Created")
                .message("Your workforce account has been created. Please check your email for login credentials.")
                .redirectType(NotificationConstants.REDIRECT_PROFILE)
                .isRead(false)
                .expiresAt(LocalDateTime.now().plusDays(NotificationConstants.RETENTION_DAYS))
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyLeaveApplied(User approver, Long leaveId, String applicantName) {
        saveNotification(
                approver,
                "Leave Applied",
                applicantName + " submitted a leave request for your approval.",
                NotificationConstants.REDIRECT_LEAVE + ":" + leaveId);
    }

    @Transactional
    public void notifyLeaveApproved(User applicant, Long leaveId) {
        saveNotification(
                applicant,
                "Leave Approved",
                "Your leave request has been approved.",
                NotificationConstants.REDIRECT_LEAVE + ":" + leaveId);
    }

    @Transactional
    public void notifyLeaveRejected(User applicant, Long leaveId) {
        saveNotification(
                applicant,
                "Leave Rejected",
                "Your leave request has been rejected.",
                NotificationConstants.REDIRECT_LEAVE + ":" + leaveId);
    }

    @Transactional
    public void notifyLeaveCancelled(User user, Long leaveId, String message) {
        saveNotification(
                user,
                "Leave Cancelled",
                message,
                NotificationConstants.REDIRECT_LEAVE + ":" + leaveId);
    }

    @Transactional
    public void notifyMissingCheckout(User employee, Long attendanceId) {
        saveNotification(
                employee,
                "Checkout Missing",
                "Please complete your checkout.",
                NotificationConstants.REDIRECT_ATTENDANCE + ":" + attendanceId);
    }

    @Transactional
    public void notifyHrMissingCheckout(User hr, String employeeName, Long attendanceId) {
        saveNotification(
                hr,
                "Checkout Missing",
                employeeName + " has not completed checkout.",
                NotificationConstants.REDIRECT_ATTENDANCE + ":" + attendanceId);
    }

    @Transactional
    public void notifyPayrollGenerated(User employee, Long payrollId) {
        saveNotification(
                employee,
                "Payroll Generated",
                "Your payroll has been generated.",
                NotificationConstants.REDIRECT_PAYROLL + ":" + payrollId);
    }

    @Transactional
    public void notifySalaryCredited(User employee, Long payrollId) {
        saveNotification(
                employee,
                "Salary Credited",
                "Your salary has been credited.",
                NotificationConstants.REDIRECT_PAYROLL + ":" + payrollId);
    }

    @Transactional
    public void notifyPayslipGenerated(User employee, Long payslipId) {
        saveNotification(
                employee,
                "Payslip Generated",
                "Your payslip is available for download.",
                NotificationConstants.REDIRECT_PAYSLIP + ":" + payslipId);
    }

    @Transactional
    public void notifyPayrollReminder(User recipient, int year, int month) {
        saveNotification(
                recipient,
                "Payroll Reminder",
                "Payroll for " + month + "/" + year + " has not been generated yet.",
                NotificationConstants.REDIRECT_PAYROLL);
    }

    @Transactional
    public void notifyLeaveConvertedToUnpaid(User applicant, Long leaveId) {
        saveNotification(
                applicant,
                "Leave Converted To Unpaid",
                "Your approved leave has been converted to unpaid leave.",
                NotificationConstants.REDIRECT_LEAVE + ":" + leaveId);
    }

    private void saveNotification(User user, String title, String message, String redirectType) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .redirectType(redirectType)
                .isRead(false)
                .expiresAt(LocalDateTime.now().plusDays(NotificationConstants.RETENTION_DAYS))
                .build();
        notificationRepository.save(notification);
    }
}
