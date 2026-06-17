package com.company.payroll.service;

import com.company.payroll.dto.settings.SystemSettingsResponseDto;
import com.company.payroll.dto.settings.UpdateAttendanceSettingsRequestDto;
import com.company.payroll.dto.settings.UpdateCompanySettingsRequestDto;
import com.company.payroll.dto.settings.UpdateLeaveSettingsRequestDto;
import com.company.payroll.dto.settings.UpdatePayrollSettingsRequestDto;
import com.company.payroll.entity.SystemSettings;
import com.company.payroll.exception.ResourceNotFoundException;
import com.company.payroll.exception.ValidationException;
import com.company.payroll.mapper.SystemSettingsMapper;
import com.company.payroll.repository.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemSettingsService {

    private final SystemSettingsRepository systemSettingsRepository;
    private final SystemSettingsMapper systemSettingsMapper;

    @Transactional(readOnly = true)
    public SystemSettingsResponseDto getSystemSettings() {
        return systemSettingsMapper.toResponse(requireSettings());
    }

    @Transactional(readOnly = true)
    public SystemSettings requireSettings() {
        return systemSettingsRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException("Settings Not Found"));
    }

    @Transactional
    public SystemSettingsResponseDto updateCompanySettings(UpdateCompanySettingsRequestDto request) {
        SystemSettings settings = requireSettings();
        settings.setCompanyName(request.getCompanyName());
        settings.setCompanyAddress(request.getCompanyAddress());
        settings.setCompanyEmail(request.getCompanyEmail());
        settings.setCompanyPhone(request.getCompanyPhone());
        return systemSettingsMapper.toResponse(systemSettingsRepository.save(settings));
    }

    @Transactional
    public SystemSettingsResponseDto updateAttendanceSettings(UpdateAttendanceSettingsRequestDto request) {
        if (!request.getOfficeStartTime().isBefore(request.getOfficeEndTime())) {
            throw new ValidationException("Invalid Time Range");
        }

        SystemSettings settings = requireSettings();
        settings.setOfficeStartTime(request.getOfficeStartTime());
        settings.setOfficeEndTime(request.getOfficeEndTime());
        settings.setCheckoutReminderTime(
                systemSettingsMapper.computeCheckoutReminderTime(
                        request.getOfficeEndTime(),
                        request.getCheckoutReminderHours()));
        return systemSettingsMapper.toResponse(systemSettingsRepository.save(settings));
    }

    @Transactional
    public SystemSettingsResponseDto updateLeaveSettings(UpdateLeaveSettingsRequestDto request) {
        SystemSettings settings = requireSettings();
        settings.setAnnualPaidLeaveLimit(request.getAnnualPaidLeaveLimit());
        return systemSettingsMapper.toResponse(systemSettingsRepository.save(settings));
    }

    @Transactional
    public SystemSettingsResponseDto updatePayrollSettings(UpdatePayrollSettingsRequestDto request) {
        SystemSettings settings = requireSettings();
        settings.setPfPercentage(request.getPfPercentage());
        settings.setSalaryCreditDay(request.getSalaryCreditDay());
        return systemSettingsMapper.toResponse(systemSettingsRepository.save(settings));
    }
}
