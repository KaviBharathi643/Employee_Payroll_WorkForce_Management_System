package com.company.payroll.dto.employee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateEmployeeRequestDto {

    @NotBlank(message = "Full name is required")
    private String fullName;

    /*@NotBlank(message = "Phone is required")
    private String phone;*/
    @NotBlank(message = "Phone is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone number must contain exactly 10 digits")
    
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than zero")
    private BigDecimal basicSalary;

    @NotBlank(message = "Employment type is required")
    @Pattern(regexp = "FULL_TIME|INTERN", message = "Employment type must be FULL_TIME or INTERN")
    private String employmentType;
}
