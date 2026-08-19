package com.company.ems.controller;

import com.company.ems.dto.request.CreateEmployeeRequest;
import com.company.ems.dto.request.UpdateEmployeeDepartmentRequest;
import com.company.ems.dto.request.UpdateEmployeeRequest;
import com.company.ems.dto.response.EmployeeLookupResponse;
import com.company.ems.dto.response.EmployeeResponse;
import com.company.ems.dto.response.EmployeeSummaryResponse;
import com.company.ems.dto.response.PagedResponse;
import com.company.ems.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Employee management APIs")
public class EmployeeController {

    private final EmployeeService employeeService;

    // ----------------------------------------------------------------
    // POST /api/v1/employees
    // ----------------------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new employee",
            description = "Creates a new employee. Employee code is auto-generated.")
    public EmployeeResponse createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    // ----------------------------------------------------------------
    // PUT /api/v1/employees/{id}
    // ----------------------------------------------------------------
    @PutMapping("/{id}")
    @Operation(summary = "Update an employee",
            description = "Updates all fields of an existing employee.")
    public EmployeeResponse updateEmployee(
            @Parameter(description = "Employee ID") @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        return employeeService.updateEmployee(id, request);
    }

    // ----------------------------------------------------------------
    // GET /api/v1/employees/{id}
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID",
            description = "Returns full employee details by ID.")
    public EmployeeResponse getEmployeeById(
            @Parameter(description = "Employee ID") @PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    // ----------------------------------------------------------------
    // GET /api/v1/employees
    // GET /api/v1/employees?lookup=true
    // ----------------------------------------------------------------
    @GetMapping
    @Operation(
            summary = "Get all employees (with optional lookup mode)",
            description = """
                    Returns a paginated list of employees.
                    - Default: full employee details.
                    - With `?lookup=true`: returns lightweight id+name pairs only.
                    
                    Pagination parameters: page (default 0), size (default 20).
                    """
    )
    public ResponseEntity<?> getAllEmployees(
            @Parameter(description = "If true, returns lightweight id+name lookup list")
            @RequestParam(defaultValue = "false") boolean lookup,
            @PageableDefault(size = 20, page = 0) Pageable pageable) {

        if (lookup) {
            PagedResponse<EmployeeLookupResponse> lookupResponse = employeeService.getEmployeeLookup(pageable);
            return ResponseEntity.ok(lookupResponse);
        }

        PagedResponse<EmployeeResponse> fullResponse = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(fullResponse);
    }

    // ----------------------------------------------------------------
    // PATCH /api/v1/employees/{id}/department
    // ----------------------------------------------------------------
    @PatchMapping("/{id}/department")
    @Operation(summary = "Update employee's department",
            description = "Updates only the department assignment for an existing employee.")
    public EmployeeResponse updateEmployeeDepartment(
            @Parameter(description = "Employee ID") @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeDepartmentRequest request) {
        return employeeService.updateEmployeeDepartment(id, request);
    }

    // ----------------------------------------------------------------
    // GET /api/v1/employees/{id}/reporting-chain
    // ----------------------------------------------------------------
    @GetMapping("/{id}/reporting-chain")
    @Operation(
            summary = "Get reporting chain",
            description = """
                    Returns the complete reporting hierarchy starting from the given employee
                    up to the top-level manager (CEO).
                    Example: Employee → Manager → Director → VP → CEO
                    """
    )
    public List<EmployeeSummaryResponse> getReportingChain(
            @Parameter(description = "Employee ID") @PathVariable Long id) {
        return employeeService.getReportingChain(id);
    }
}
