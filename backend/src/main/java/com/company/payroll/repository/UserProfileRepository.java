package com.company.payroll.repository;

import com.company.payroll.entity.User;
import com.company.payroll.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long>, JpaSpecificationExecutor<UserProfile> {

    Optional<UserProfile> findByUser(User user);

    Optional<UserProfile> findByUserId(Long userId);

    Optional<UserProfile> findByEmployeeCode(String employeeCode);

    boolean existsByEmployeeCode(String employeeCode);

    Optional<UserProfile> findTopByEmployeeCodeStartingWithOrderByEmployeeCodeDesc(String prefix);
}
