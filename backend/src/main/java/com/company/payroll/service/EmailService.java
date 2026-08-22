package com.company.payroll.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@company.com}")
    private String fromEmail;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Autowired
    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAccountCreatedEmail(String toEmail, String fullName, String temporaryPassword) {
        String subject = "Your Account Has Been Created";
        String body = "Hello " + fullName + ",\n\nYour account has been created.\n"
                + "Email: " + toEmail + "\n"
                + "Temporary Password: " + temporaryPassword + "\n\n"
                + "Please log in and change your password.";
        
        if (!mailEnabled || mailSender == null) {
            log.info("[SIMULATION] Email not sent (mail.enabled is false). To: {}, Subject: {}\nBody:\n{}", toEmail, subject, body);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send account created email to {}", toEmail, ex);
        }
    }

    public void sendPayslipGeneratedEmail(String toEmail) {
        sendSimpleEmail(toEmail, "Payslip Generated",
                "Your payslip is available for download. Please log in to the portal.");
    }

    public void sendPayrollGeneratedEmail(String toEmail) {
        sendSimpleEmail(toEmail, "Payroll Generated", "Your payroll has been generated. Please log in to view details.");
    }

    public void sendSalaryCreditedEmail(String toEmail) {
        sendSimpleEmail(toEmail, "Salary Credited", "Your salary has been credited.");
    }

    public void sendPayrollReminderEmail(String toEmail, int year, int month) {
        sendSimpleEmail(toEmail, "Payroll Reminder",
                "Reminder: Payroll for " + month + "/" + year + " has not been generated yet.");
    }

    public void sendMissingCheckoutEmail(String toEmail) {
        sendSimpleEmail(toEmail, "Checkout Missing", "Please complete your checkout for today.");
    }

    public void sendLeaveAppliedEmail(String toEmail, String applicantName) {
        sendSimpleEmail(toEmail, "Leave Application Submitted",
                applicantName + " has submitted a leave request pending your approval.");
    }

    public void sendLeaveApprovedEmail(String toEmail) {
        sendSimpleEmail(toEmail, "Leave Approved", "Your leave request has been approved.");
    }

    public void sendLeaveRejectedEmail(String toEmail) {
        sendSimpleEmail(toEmail, "Leave Rejected", "Your leave request has been rejected.");
    }

    public void sendLeaveCancelledEmail(String toEmail) {
        sendSimpleEmail(toEmail, "Leave Cancelled", "A leave request has been cancelled.");
    }

    public void sendLeaveConvertedEmail(String toEmail) {
        sendSimpleEmail(toEmail, "Leave Converted To Unpaid",
                "Your leave has been converted to unpaid leave.");
    }

    private void sendSimpleEmail(String toEmail, String subject, String text) {
        if (!mailEnabled || mailSender == null) {
            log.info("[SIMULATION] Email not sent (mail.enabled is false). To: {}, Subject: {}\nBody: {}", toEmail, subject, text);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send email to {}", toEmail, ex);
        }
    }

    public void sendOtpEmail(String toEmail, String otpCode) {
        String subject = "Password Reset OTP";
        String body = "Your password reset OTP is: " + otpCode + "\nThis OTP expires in 2 minutes.";

        if (!mailEnabled || mailSender == null) {
            log.info("[SIMULATION] Email not sent (mail.enabled is false). To: {}, Subject: {}\nBody: {}", toEmail, subject, body);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send OTP email to {}", toEmail, ex);
        }
    }
}
