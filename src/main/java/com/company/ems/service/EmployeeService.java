package com.company.ems.service;

import com.company.ems.dto.request.CreateEmployeeRequest;
import com.company.ems.dto.request.UpdateEmployeeDepartmentRequest;
import com.company.ems.dto.request.UpdateEmployeeRequest;
import com.company.ems.dto.response.EmployeeLookupResponse;
import com.company.ems.dto.response.EmployeeResponse;
import com.company.ems.dto.response.EmployeeSummaryResponse;
import com.company.ems.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    /**
     * Creates a new employee. The employee code is auto-generated.
     */
    EmployeeResponse createEmployee(CreateEmployeeRequest request);

    /**
     * Updates all fields of an existing employee by ID.
     */
    EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request);

    /**
     * Retrieves a single employee by ID.
     */
    EmployeeResponse getEmployeeById(Long id);

    /**
     * Returns a paginated list of all employees with full details.
     */
    PagedResponse<EmployeeResponse> getAllEmployees(Pageable pageable);

    /**
     * Returns a paginated lightweight list of employees (id + name only).
     * Used for lookup dropdowns and autocomplete.
     */
    PagedResponse<EmployeeLookupResponse> getEmployeeLookup(Pageable pageable);

    /**
     * Updates only the department of an existing employee.
     */
    EmployeeResponse updateEmployeeDepartment(Long id, UpdateEmployeeDepartmentRequest request);

    /**
     * Returns the full reporting hierarchy from the given employee up to the top-level manager.
     * Uses a PostgreSQL recursive CTE for efficient single-query traversal.
     */
    List<EmployeeSummaryResponse> getReportingChain(Long id);
}
