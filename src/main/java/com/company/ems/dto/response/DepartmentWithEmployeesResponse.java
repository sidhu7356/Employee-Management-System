package com.company.ems.dto.response;

import lombok.*;

import java.util.List;

/**
 * Response for GET /departments/{id}?expand=employee
 * Contains the full department details along with its employees list.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentWithEmployeesResponse {

    private DepartmentResponse department;
    private List<EmployeeResponse> employees;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
