package com.company.payroll.util;

import com.company.payroll.entity.Payslip;
import com.company.payroll.entity.UserProfile;
import com.company.payroll.repository.PayslipRepository;
import com.company.payroll.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CodeGeneratorUtil {

    private static final Pattern EMP_PATTERN = Pattern.compile("^EMP(\\d+)$");
    private static final Pattern HR_PATTERN = Pattern.compile("^HR(\\d+)$");
    private static final Pattern PSL_PATTERN = Pattern.compile("^PSL-(\\d{4})-(\\d{4})$");

    private final UserProfileRepository userProfileRepository;
    private final PayslipRepository payslipRepository;

    public String generateEmployeeCode() {
        return nextCode("EMP", EMP_PATTERN);
    }

    public String generateHrCode() {
        return nextCode("HR", HR_PATTERN);
    }

    public String generatePayslipNumber() {
        int year = Year.now().getValue();
        String prefix = "PSL-" + year + "-";
        Optional<Payslip> latest = payslipRepository.findTopByPayslipNumberStartingWithOrderByPayslipNumberDesc(prefix);
        if (latest.isEmpty()) {
            return prefix + "0001";
        }
        Matcher matcher = PSL_PATTERN.matcher(latest.get().getPayslipNumber());
        if (!matcher.matches() || Integer.parseInt(matcher.group(1)) != year) {
            return prefix + "0001";
        }
        int next = Integer.parseInt(matcher.group(2)) + 1;
        return String.format("PSL-%d-%04d", year, next);
    }

    private String nextCode(String prefix, Pattern pattern) {
        Optional<UserProfile> latest = userProfileRepository
                .findTopByEmployeeCodeStartingWithOrderByEmployeeCodeDesc(prefix);
        if (latest.isEmpty()) {
            return prefix + "001";
        }
        Matcher matcher = pattern.matcher(latest.get().getEmployeeCode());
        if (!matcher.matches()) {
            return prefix + "001";
        }
        int next = Integer.parseInt(matcher.group(1)) + 1;
        return String.format("%s%03d", prefix, next);
    }
}
