package com.company.payroll.dto.settings;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCompanySettingsRequestDto {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Company address is required")
    private String companyAddress;

    @NotBlank(message = "Company email is required")
    @Email(message = "Valid company email is required")
    private String companyEmail;

    @NotBlank(message = "Company phone is required")
    @Size(max = 20, message = "Company phone must not exceed 20 characters")
    private String companyPhone;
}
