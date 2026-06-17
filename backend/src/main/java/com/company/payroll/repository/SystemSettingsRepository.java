package com.company.payroll.repository;

import com.company.payroll.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {

    Optional<SystemSettings> findFirstByOrderByIdAsc();
}
