package com.company.ems.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDepartmentRequest {

    @NotBlank(message = "Department name is mandatory")
    @Size(max = 255, message = "Department name must not exceed 255 characters")
    private String name;

    @NotNull(message = "Creation date is required")
    @PastOrPresent(message = "Creation date cannot be a future date")
    private LocalDate creationDate;

    /**
     * Optional: the employee ID of the department head.
     * Set to NULL to clear the department head.
     * If provided, the employee must exist — validated in service layer.
     */
    private Long departmentHeadId;
}
