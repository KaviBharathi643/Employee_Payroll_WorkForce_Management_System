package com.company.payroll.repository;

import com.company.payroll.entity.User;
import com.company.payroll.entity.UserBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBankDetailsRepository extends JpaRepository<UserBankDetails, Long> {

    Optional<UserBankDetails> findByUser(User user);

    Optional<UserBankDetails> findByUserId(Long userId);
}
