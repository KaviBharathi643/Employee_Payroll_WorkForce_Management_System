package com.company.payroll.service;

import com.company.payroll.constants.PayrollConstants;
import com.company.payroll.constants.RoleConstants;
import com.company.payroll.dto.payslip.PayslipListResponseDto;
import com.company.payroll.dto.payslip.PayslipResponseDto;
import com.company.payroll.entity.Payroll;
import com.company.payroll.entity.Payslip;
import com.company.payroll.entity.SystemSettings;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserBankDetails;
import com.company.payroll.exception.ResourceNotFoundException;
import com.company.payroll.mapper.PayslipMapper;
import com.company.payroll.repository.PayrollRepository;
import com.company.payroll.repository.PayslipRepository;
import com.company.payroll.repository.UserBankDetailsRepository;
import com.company.payroll.security.CurrentUserService;
import com.company.payroll.util.CodeGeneratorUtil;
import com.company.payroll.util.PayslipPdfStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayslipService {

    private final PayrollRepository payrollRepository;
    private final PayslipRepository payslipRepository;
    private final UserBankDetailsRepository userBankDetailsRepository;
    private final CodeGeneratorUtil codeGeneratorUtil;
    private final NotificationHelperService notificationHelperService;
    private final EmailService emailService;
    private final PdfService pdfService;
    private final PayslipPdfStorageUtil payslipPdfStorageUtil;
    private final SystemSettingsService systemSettingsService;
    private final PayslipMapper payslipMapper;
    private final CurrentUserService currentUserService;

    @Transactional
    public void generatePayslip(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll Not Found"));

        Payslip payslip = payslipRepository.findByPayrollId(payrollId).orElseGet(() -> {
            Payslip newPayslip = Payslip.builder()
                    .payroll(payroll)
                    .payslipNumber(codeGeneratorUtil.generatePayslipNumber())
                    .pdfGenerated(false)
                    .generatedDate(LocalDateTime.now())
                    .status(PayrollConstants.GENERATED)
                    .build();
            return payslipRepository.save(newPayslip);
        });

        boolean firstGeneration = Boolean.FALSE.equals(payslip.getPdfGenerated());
        generateAndStorePdf(payslip, payroll);

        if (firstGeneration) {
            notificationHelperService.notifyPayslipGenerated(payroll.getUser(), payslip.getId());
            emailService.sendPayslipGeneratedEmail(payroll.getUser().getEmail());
        }
    }

    @Transactional(readOnly = true)
    public List<PayslipListResponseDto> getPayslipList() {
        User current = currentUserService.getCurrentUser();
        List<Payslip> payslips = switch (current.getRole()) {
            case RoleConstants.EMPLOYEE -> payslipRepository.findByPayrollUserIdOrderByGeneratedDateDesc(current.getId());
            case RoleConstants.HR -> payslipRepository.findByPayrollUserRoleOrderByGeneratedDateDesc(RoleConstants.EMPLOYEE);
            case RoleConstants.ADMIN -> payslipRepository.findByPayrollUserRoleOrderByGeneratedDateDesc(RoleConstants.HR);
            default -> throw new AccessDeniedException("Access Denied");
        };
        return payslips.stream().map(payslipMapper::toListItem).toList();
    }

    @Transactional(readOnly = true)
    public PayslipResponseDto getPayslipDetails(Long payslipId) {
        Payslip payslip = getAccessiblePayslip(payslipId);
        UserBankDetails bank = userBankDetailsRepository.findByUser(payslip.getPayroll().getUser()).orElse(null);
        return payslipMapper.toResponse(payslip, bank);
    }

    @Transactional
    public PayslipDownloadResult downloadPayslip(Long payslipId) {
        Payslip payslip = getAccessiblePayslip(payslipId);
        byte[] pdf = payslipPdfStorageUtil.readPdf(payslip.getPayslipNumber());
        if (pdf == null) {
            pdf = generateAndStorePdf(payslip, payslip.getPayroll());
        }
        if (pdf == null || pdf.length == 0) {
            throw new ResourceNotFoundException("PDF Not Found");
        }
        return new PayslipDownloadResult(payslip.getPayslipNumber() + ".pdf", pdf);
    }

    public record PayslipDownloadResult(String filename, byte[] content) {
    }

    private byte[] generateAndStorePdf(Payslip payslip, Payroll payroll) {
        SystemSettings settings = systemSettingsService.requireSettings();
        UserBankDetails bank = userBankDetailsRepository.findByUser(payroll.getUser()).orElse(null);
        byte[] pdfBytes = pdfService.createPayslipPdf(settings, payroll, payslip, bank);
        payslipPdfStorageUtil.savePdf(payslip.getPayslipNumber(), pdfBytes);
        payslip.setPdfGenerated(true);
        payslipRepository.save(payslip);
        return pdfBytes;
    }

    private Payslip getAccessiblePayslip(Long payslipId) {
        Payslip payslip = payslipRepository.findByIdWithPayroll(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip Not Found"));
        validateAccess(currentUserService.getCurrentUser(), payslip);
        return payslip;
    }

    private void validateAccess(User viewer, Payslip payslip) {
        Payroll payroll = payslip.getPayroll();
        User owner = payroll.getUser();

        if (RoleConstants.EMPLOYEE.equals(viewer.getRole())) {
            if (!owner.getId().equals(viewer.getId())) {
                throw new AccessDeniedException("Unauthorized Access");
            }
            return;
        }
        if (RoleConstants.HR.equals(viewer.getRole())) {
            if (!RoleConstants.EMPLOYEE.equals(owner.getRole())) {
                throw new AccessDeniedException("Unauthorized Access");
            }
            return;
        }
        if (RoleConstants.ADMIN.equals(viewer.getRole())) {
            if (!RoleConstants.HR.equals(owner.getRole())) {
                throw new AccessDeniedException("Unauthorized Access");
            }
            return;
        }
        throw new AccessDeniedException("Unauthorized Access");
    }
}
