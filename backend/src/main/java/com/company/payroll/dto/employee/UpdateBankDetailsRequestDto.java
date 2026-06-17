package com.company.payroll.dto.employee;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateBankDetailsRequestDto {

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "IFSC code is required")
    private String ifscCode;
}
