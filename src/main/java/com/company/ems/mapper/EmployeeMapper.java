package com.company.ems.mapper;

import com.company.ems.dto.request.CreateEmployeeRequest;
import com.company.ems.dto.request.UpdateEmployeeRequest;
import com.company.ems.dto.response.DepartmentSummaryResponse;
import com.company.ems.dto.response.EmployeeLookupResponse;
import com.company.ems.dto.response.EmployeeResponse;
import com.company.ems.dto.response.EmployeeSummaryResponse;
import com.company.ems.entity.Department;
import com.company.ems.entity.Employee;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Manual mapper for converting between Employee entity and DTOs.
 * Handles circular-reference-safe mapping by using lightweight summary DTOs
 * for nested objects (department, reportingManager).
 */
@Component
public class EmployeeMapper {

    // ----------------------------------------------------------------
    // Entity → Response DTOs
    // ----------------------------------------------------------------

    public EmployeeResponse toResponse(Employee employee) {
        if (employee == null) return null;

        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .name(employee.getName())
                .dateOfBirth(employee.getDateOfBirth())
                .salary(employee.getSalary())
                .department(toDepartmentSummary(employee.getDepartment()))
                .address(employee.getAddress())
                .roleTitle(employee.getRoleTitle())
                .joiningDate(employee.getJoiningDate())
                .yearlyBonusPercentage(employee.getYearlyBonusPercentage())
                .reportingManager(toSummaryResponse(employee.getReportingManager()))
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

    public EmployeeSummaryResponse toSummaryResponse(Employee employee) {
        if (employee == null) return null;

        return EmployeeSummaryResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .roleTitle(employee.getRoleTitle())
                .build();
    }

    public EmployeeLookupResponse toLookupResponse(Employee employee) {
        if (employee == null) return null;
        return new EmployeeLookupResponse(employee.getId(), employee.getName());
    }

    // ----------------------------------------------------------------
    // Request DTOs → Entity
    // ----------------------------------------------------------------

    /**
     * Creates a new Employee entity from a CreateEmployeeRequest.
     * Department and ReportingManager are passed separately (fetched from DB).
     */
    public Employee toEntity(CreateEmployeeRequest request, Department department, Employee reportingManager) {
        return Employee.builder()
                .employeeCode(generateEmployeeCode())
                .name(request.getName())
                .dateOfBirth(request.getDateOfBirth())
                .salary(request.getSalary())
                .department(department)
                .address(request.getAddress())
                .roleTitle(request.getRoleTitle())
                .joiningDate(request.getJoiningDate())
                .yearlyBonusPercentage(request.getYearlyBonusPercentage())
                .reportingManager(reportingManager)
                .build();
    }

    /**
     * Updates an existing Employee entity in-place from an UpdateEmployeeRequest.
     */
    public void updateEntity(Employee employee, UpdateEmployeeRequest request,
                             Department department, Employee reportingManager) {
        employee.setName(request.getName());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setSalary(request.getSalary());
        employee.setDepartment(department);
        employee.setAddress(request.getAddress());
        employee.setRoleTitle(request.getRoleTitle());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setYearlyBonusPercentage(request.getYearlyBonusPercentage());
        employee.setReportingManager(reportingManager);
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private DepartmentSummaryResponse toDepartmentSummary(Department department) {
        if (department == null) return null;
        return DepartmentSummaryResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }

    private String generateEmployeeCode() {
        return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
