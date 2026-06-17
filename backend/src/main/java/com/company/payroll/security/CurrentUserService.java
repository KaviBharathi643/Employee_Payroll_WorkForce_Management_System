package com.company.payroll.security;

import com.company.payroll.entity.User;
import com.company.payroll.exception.UnauthorizedException;
import com.company.payroll.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) {
            throw new UnauthorizedException("Unauthorized Access");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized Access"));
    }
}
