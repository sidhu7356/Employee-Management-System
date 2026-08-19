package com.company.ems.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    private Long id;
    private String name;
    private LocalDate creationDate;
    private EmployeeSummaryResponse departmentHead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
