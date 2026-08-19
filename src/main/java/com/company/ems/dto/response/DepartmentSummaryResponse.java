package com.company.ems.dto.response;

import lombok.*;

/**
 * Concise department summary used as a nested object within employee responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentSummaryResponse {

    private Long id;
    private String name;
}
