package com.company.ems.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Long id;
    private String employeeCode;
    private String name;
    private LocalDate dateOfBirth;
    private BigDecimal salary;
    private DepartmentSummaryResponse department;
    private String address;
    private String roleTitle;
    private LocalDate joiningDate;
    private BigDecimal yearlyBonusPercentage;
    private EmployeeSummaryResponse reportingManager;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
