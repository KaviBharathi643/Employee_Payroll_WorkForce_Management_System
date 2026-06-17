package com.company.payroll.repository;

import com.company.payroll.entity.User;
import com.company.payroll.entity.UserEmployment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEmploymentRepository extends JpaRepository<UserEmployment, Long> {

    Optional<UserEmployment> findByUser(User user);

    Optional<UserEmployment> findByUserId(Long userId);
}
