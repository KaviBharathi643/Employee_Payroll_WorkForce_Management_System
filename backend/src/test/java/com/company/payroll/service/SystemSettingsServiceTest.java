package com.company.payroll.service;

import com.company.payroll.dto.settings.UpdateAttendanceSettingsRequestDto;
import com.company.payroll.entity.SystemSettings;
import com.company.payroll.exception.ValidationException;
import com.company.payroll.mapper.SystemSettingsMapper;
import com.company.payroll.repository.SystemSettingsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemSettingsServiceTest {

    @Mock
    private SystemSettingsRepository systemSettingsRepository;
    @Mock
    private SystemSettingsMapper systemSettingsMapper;

    @InjectMocks
    private SystemSettingsService systemSettingsService;

    @Test
    void updateAttendanceSettings_invalidTimeRange() {
        SystemSettings settings = new SystemSettings();
        when(systemSettingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(settings));

        UpdateAttendanceSettingsRequestDto request = new UpdateAttendanceSettingsRequestDto();
        request.setOfficeStartTime(LocalTime.of(18, 0));
        request.setOfficeEndTime(LocalTime.of(9, 0));
        request.setCheckoutReminderHours(1);

        assertThrows(ValidationException.class, () -> systemSettingsService.updateAttendanceSettings(request));
    }
}
