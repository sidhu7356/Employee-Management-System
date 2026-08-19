package com.company.ems.service;

import com.company.ems.dto.request.CreateDepartmentRequest;
import com.company.ems.dto.request.UpdateDepartmentRequest;
import com.company.ems.dto.response.DepartmentAnalyticsResponse;
import com.company.ems.dto.response.DepartmentResponse;
import com.company.ems.dto.response.DepartmentWithEmployeesResponse;
import com.company.ems.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    /**
     * Creates a new department. DepartmentHead is optional.
     */
    DepartmentResponse createDepartment(CreateDepartmentRequest request);

    /**
     * Updates all fields of an existing department by ID.
     */
    DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request);

    /**
     * Deletes a department. Fails with BusinessRuleViolationException if the department has employees.
     */
    void deleteDepartment(Long id);

    /**
     * Retrieves a single department by ID.
     */
    DepartmentResponse getDepartmentById(Long id);

    /**
     * Returns a paginated list of all departments.
     */
    PagedResponse<DepartmentResponse> getAllDepartments(Pageable pageable);

    /**
     * Returns a department along with its paginated list of employees.
     * Used for GET /departments/{id}?expand=employee
     */
    DepartmentWithEmployeesResponse getDepartmentWithEmployees(Long id, Pageable pageable);

    /**
     * Returns analytics (employee count, average salary, total salary) for a department.
     */
    DepartmentAnalyticsResponse getDepartmentAnalytics(Long id);
}
