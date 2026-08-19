package com.company.ems.mapper;

import com.company.ems.dto.request.CreateDepartmentRequest;
import com.company.ems.dto.request.UpdateDepartmentRequest;
import com.company.ems.dto.response.DepartmentResponse;
import com.company.ems.dto.response.EmployeeSummaryResponse;
import com.company.ems.entity.Department;
import com.company.ems.entity.Employee;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting between Department entity and DTOs.
 */
@Component
public class DepartmentMapper {

    // ----------------------------------------------------------------
    // Entity → Response DTOs
    // ----------------------------------------------------------------

    public DepartmentResponse toResponse(Department department) {
        if (department == null) return null;

        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .creationDate(department.getCreationDate())
                .departmentHead(toEmployeeSummary(department.getDepartmentHead()))
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }

    // ----------------------------------------------------------------
    // Request DTOs → Entity
    // ----------------------------------------------------------------

    /**
     * Creates a new Department entity from a CreateDepartmentRequest.
     * DepartmentHead is passed separately (fetched from DB, may be null).
     */
    public Department toEntity(CreateDepartmentRequest request, Employee departmentHead) {
        return Department.builder()
                .name(request.getName())
                .creationDate(request.getCreationDate())
                .departmentHead(departmentHead)
                .build();
    }

    /**
     * Updates an existing Department entity in-place from an UpdateDepartmentRequest.
     */
    public void updateEntity(Department department, UpdateDepartmentRequest request, Employee departmentHead) {
        department.setName(request.getName());
        department.setCreationDate(request.getCreationDate());
        department.setDepartmentHead(departmentHead);
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private EmployeeSummaryResponse toEmployeeSummary(Employee employee) {
        if (employee == null) return null;
        return EmployeeSummaryResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .roleTitle(employee.getRoleTitle())
                .build();
    }
}
