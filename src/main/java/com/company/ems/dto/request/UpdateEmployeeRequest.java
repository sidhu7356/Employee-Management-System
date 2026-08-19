package com.company.ems.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEmployeeRequest {

    @NotBlank(message = "Employee name is mandatory")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.01", message = "Salary must be greater than zero")
    @Digits(integer = 13, fraction = 2, message = "Salary must be a valid monetary amount")
    private BigDecimal salary;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    private String address;

    @NotBlank(message = "Role title is mandatory")
    @Size(max = 255, message = "Role title must not exceed 255 characters")
    private String roleTitle;

    @NotNull(message = "Joining date is required")
    @PastOrPresent(message = "Joining date cannot be a future date")
    private LocalDate joiningDate;

    @NotNull(message = "Yearly bonus percentage is required")
    @DecimalMin(value = "0.0", message = "Bonus percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Bonus percentage must not exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Bonus percentage must be a valid number")
    private BigDecimal yearlyBonusPercentage;

    /**
     * Set to NULL to remove the reporting manager (only valid for top-level employees).
     * Validated in service layer.
     */
    private Long reportingManagerId;
}
