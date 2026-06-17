package com.company.payroll.mapper;

import com.company.payroll.dto.payroll.PayrollResponseDto;
import com.company.payroll.entity.Payroll;
import org.springframework.stereotype.Component;

@Component
public class PayrollMapper {

    public PayrollResponseDto toResponse(Payroll payroll) {
        return PayrollResponseDto.builder()
                .payrollId(payroll.getId())
                .userId(payroll.getUser().getId())
                .payrollYear(payroll.getPayrollYear())
                .payrollMonth(payroll.getPayrollMonth())
                .employeeCode(payroll.getEmployeeCode())
                .employeeName(payroll.getEmployeeName())
                .department(payroll.getDepartment())
                .designation(payroll.getDesignation())
                .basicSalary(payroll.getBasicSalary())
                .bonus(payroll.getBonus())
                .pfPercentage(payroll.getPfPercentage())
                .pfAmount(payroll.getPfAmount())
                .unpaidLeaveCount(payroll.getUnpaidLeaveCount())
                .unpaidLeaveDeduction(payroll.getUnpaidLeaveDeduction())
                .finalSalary(payroll.getFinalSalary())
                .status(payroll.getStatus())
                .generatedDate(payroll.getGeneratedDate())
                .creditedDate(payroll.getCreditedDate())
                .build();
    }
}
