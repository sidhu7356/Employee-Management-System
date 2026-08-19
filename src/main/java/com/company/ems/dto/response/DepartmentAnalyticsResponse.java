package com.company.ems.dto.response;

import lombok.*;

import java.math.BigDecimal;

/**
 * Response for GET /departments/{id}/analytics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentAnalyticsResponse {

    private Long departmentId;
    private long employeeCount;
    private BigDecimal averageSalary;
    private BigDecimal totalSalary;
}
