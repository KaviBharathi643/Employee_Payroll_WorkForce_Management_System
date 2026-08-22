package com.company.payroll.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PasswordGeneratorUtil {

    private static final String UPPERCASE = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijkmnpqrstuvwxyz";
    private static final String DIGITS = "23456789";
    private static final String SPECIAL = "@#$%&*!";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;

    private static final int DEFAULT_LENGTH = 10;
    private static final int MIN_LENGTH = 8;

    private final SecureRandom random = new SecureRandom();

    /**
     * Generates a temporary password with default length (10 chars)
     * containing at least one uppercase, lowercase, digit, and special char.
     */
    public String generateTemporaryPassword() {
        return generateTemporaryPassword(DEFAULT_LENGTH);
    }

    /**
     * Generates a temporary password with guaranteed character diversity.
     *
     * @param length Desired password length (minimum 8)
     * @return Generated temporary password string
     */
    public String generateTemporaryPassword(int length) {
        int effectiveLength = Math.max(length, MIN_LENGTH);

        List<Character> passwordChars = new ArrayList<>(effectiveLength);

        // Guarantee at least one character from each set
        passwordChars.add(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        passwordChars.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        passwordChars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        passwordChars.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Fill remaining characters from entire character pool
        for (int i = 4; i < effectiveLength; i++) {
            passwordChars.add(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Shuffle characters for randomness
        Collections.shuffle(passwordChars, random);

        StringBuilder sb = new StringBuilder(effectiveLength);
        for (char c : passwordChars) {
            sb.append(c);
        }

        return sb.toString();
    }
}

