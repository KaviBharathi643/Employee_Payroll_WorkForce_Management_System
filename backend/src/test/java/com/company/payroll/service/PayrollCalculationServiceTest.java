package com.company.payroll.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PayrollCalculationServiceTest {

    @Mock
    private com.company.payroll.repository.LeaveRequestRepository leaveRequestRepository;

    @InjectMocks
    private PayrollCalculationService payrollCalculationService;

    @Test
    void calculateFinalSalary() {
        BigDecimal result = payrollCalculationService.calculateFinalSalary(
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(4000),
                BigDecimal.valueOf(500));
        assertEquals(0, BigDecimal.valueOf(47500).compareTo(result));
    }

    @Test
    void calculatePfAmount() {
        BigDecimal result = payrollCalculationService.calculatePfAmount(
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(8));
        assertEquals(0, BigDecimal.valueOf(4000.00).compareTo(result));
    }
}
