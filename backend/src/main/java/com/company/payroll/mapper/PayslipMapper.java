package com.company.payroll.mapper;

import com.company.payroll.dto.payslip.PayslipListResponseDto;
import com.company.payroll.dto.payslip.PayslipResponseDto;
import com.company.payroll.entity.Payroll;
import com.company.payroll.entity.Payslip;
import com.company.payroll.entity.UserBankDetails;
import com.company.payroll.util.BankMaskingUtil;
import org.springframework.stereotype.Component;

@Component
public class PayslipMapper {

    public PayslipResponseDto toResponse(Payslip payslip, UserBankDetails bank) {
        Payroll payroll = payslip.getPayroll();
        return PayslipResponseDto.builder()
                .payslipId(payslip.getId())
                .payslipNumber(payslip.getPayslipNumber())
                .payrollId(payroll.getId())
                .employeeCode(payroll.getEmployeeCode())
                .employeeName(payroll.getEmployeeName())
                .department(payroll.getDepartment())
                .designation(payroll.getDesignation())
                .payrollMonth(payroll.getPayrollMonth())
                .payrollYear(payroll.getPayrollYear())
                .basicSalary(payroll.getBasicSalary())
                .bonus(payroll.getBonus())
                .pfPercentage(payroll.getPfPercentage())
                .pfAmount(payroll.getPfAmount())
                .unpaidLeaveCount(payroll.getUnpaidLeaveCount())
                .unpaidLeaveDeduction(payroll.getUnpaidLeaveDeduction())
                .finalSalary(payroll.getFinalSalary())
                .bankName(bank != null ? bank.getBankName() : null)
                .maskedAccountNumber(bank != null ? BankMaskingUtil.maskAccountNumber(bank.getAccountNumber()) : null)
                .ifscCode(bank != null ? bank.getIfscCode() : null)
                .generatedDate(payslip.getGeneratedDate())
                .pdfGenerated(payslip.getPdfGenerated())
                .build();
    }

    public PayslipListResponseDto toListItem(Payslip payslip) {
        Payroll payroll = payslip.getPayroll();
        return PayslipListResponseDto.builder()
                .payslipId(payslip.getId())
                .payslipNumber(payslip.getPayslipNumber())
                .payrollMonth(payroll.getPayrollMonth())
                .payrollYear(payroll.getPayrollYear())
                .generatedDate(payslip.getGeneratedDate())
                .employeeCode(payroll.getEmployeeCode())
                .employeeName(payroll.getEmployeeName())
                .build();
    }
}
