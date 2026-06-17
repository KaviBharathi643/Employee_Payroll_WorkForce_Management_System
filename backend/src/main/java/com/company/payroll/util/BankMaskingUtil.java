package com.company.payroll.util;

public final class BankMaskingUtil {

    private BankMaskingUtil() {
    }

    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        String lastFour = accountNumber.substring(accountNumber.length() - 4);
        return "XXXXXXXX" + lastFour;
    }
}
