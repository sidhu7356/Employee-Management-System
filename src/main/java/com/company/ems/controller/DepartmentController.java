package com.company.ems.controller;

import com.company.ems.dto.request.CreateDepartmentRequest;
import com.company.ems.dto.request.UpdateDepartmentRequest;
import com.company.ems.dto.response.DepartmentAnalyticsResponse;
import com.company.ems.dto.response.DepartmentResponse;
import com.company.ems.dto.response.DepartmentWithEmployeesResponse;
import com.company.ems.dto.response.PagedResponse;
import com.company.ems.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Department", description = "Department management APIs")
public class DepartmentController {

    private final DepartmentService departmentService;

    // ----------------------------------------------------------------
    // POST /api/v1/departments
    // ----------------------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new department",
            description = "Creates a new department. Department head is optional.")
    public DepartmentResponse createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        return departmentService.createDepartment(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a department",
            description = "Updates all fields of an existing department.")
    public DepartmentResponse updateDepartment(
            @Parameter(description = "Department ID") @PathVariable(name = "id") Long id,
            @Valid @RequestBody UpdateDepartmentRequest request) {
        return departmentService.updateDepartment(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a department",
            description = "Deletes a department. Fails with 422 if any employees are assigned to it.")
    public void deleteDepartment(
            @Parameter(description = "Department ID") @PathVariable(name = "id") Long id) {
        departmentService.deleteDepartment(id);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get department by ID (with optional employee expansion)",
            description = """
                    Returns full department details.
                    - Default: department info only.
                    - With `?expand=employee`: returns the department along with its paginated employee list.
                    
                    Pagination parameters (only when expand=employee): page (default 0), size (default 20).
                    """
    )
    public ResponseEntity<?> getDepartmentById(
            @Parameter(description = "Department ID") @PathVariable(name = "id") Long id,
            @Parameter(description = "Use 'employee' to expand the employee list")
            @RequestParam(name = "expand", required = false) String expand,
            @ParameterObject @PageableDefault(size = 20, page = 0) Pageable pageable) {

        if ("employee".equalsIgnoreCase(expand)) {
            DepartmentWithEmployeesResponse response = departmentService.getDepartmentWithEmployees(id, pageable);
            return ResponseEntity.ok(response);
        }

        DepartmentResponse response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(response);
    }

    // ----------------------------------------------------------------
    // GET /api/v1/departments
    // ----------------------------------------------------------------
    @GetMapping
    @Operation(summary = "Get all departments",
            description = "Returns a paginated list of all departments.")
    public PagedResponse<DepartmentResponse> getAllDepartments(
            @ParameterObject @PageableDefault(size = 20, page = 0) Pageable pageable) {
        return departmentService.getAllDepartments(pageable);
    }

    // ----------------------------------------------------------------
    // GET /api/v1/departments/{id}/analytics
    // ----------------------------------------------------------------
    @GetMapping("/{id}/analytics")
    @Operation(
            summary = "Get department analytics",
            description = "Returns employee count, average salary, and total salary for the department."
    )
    public DepartmentAnalyticsResponse getDepartmentAnalytics(
            @Parameter(description = "Department ID") @PathVariable(name = "id") Long id) {
        return departmentService.getDepartmentAnalytics(id);
    }
}
