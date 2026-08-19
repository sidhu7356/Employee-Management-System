package com.company.ems.dto.response;

import lombok.*;

/**
 * Concise employee summary used as a nested object in other responses
 * (e.g., reporting manager, department head).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSummaryResponse {

    private Long id;
    private String name;
    private String roleTitle;
}
