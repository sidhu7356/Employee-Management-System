package com.company.ems.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEmployeeDepartmentRequest {

    @NotNull(message = "Department ID is required")
    private Long departmentId;
}
