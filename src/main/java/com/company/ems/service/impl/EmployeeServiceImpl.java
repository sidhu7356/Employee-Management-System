package com.company.ems.service.impl;

import com.company.ems.dto.request.CreateEmployeeRequest;
import com.company.ems.dto.request.UpdateEmployeeDepartmentRequest;
import com.company.ems.dto.request.UpdateEmployeeRequest;
import com.company.ems.dto.response.EmployeeLookupResponse;
import com.company.ems.dto.response.EmployeeResponse;
import com.company.ems.dto.response.EmployeeSummaryResponse;
import com.company.ems.dto.response.PagedResponse;
import com.company.ems.entity.Department;
import com.company.ems.entity.Employee;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.mapper.EmployeeMapper;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    private static final String DEPARTMENT = "Department";

    // ----------------------------------------------------------------
    // Create
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        log.debug("Received request to create employee: name='{}', departmentId={}, reportingManagerId={}",
                request.getName(), request.getDepartmentId(), request.getReportingManagerId());

        log.debug("Validating department with id: {}", request.getDepartmentId());
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(DEPARTMENT, request.getDepartmentId()));
        log.debug("Department resolved: id={}, name='{}'", department.getId(), department.getName());

        Employee reportingManager = resolveReportingManager(request.getReportingManagerId());

        Employee employee = employeeMapper.toEntity(request, department, reportingManager);
        Employee saved = employeeRepository.save(employee);

        log.info("Employee created successfully: id={}, code='{}', name='{}', department='{}', role='{}'",
                saved.getId(), saved.getEmployeeCode(), saved.getName(),
                department.getName(), saved.getRoleTitle());
        return employeeMapper.toResponse(saved);
    }

    // ----------------------------------------------------------------
    // Update
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        log.debug("Received request to update employee: id={}, newName='{}', newDepartmentId={}, newRole='{}'",
                id, request.getName(), request.getDepartmentId(), request.getRoleTitle());

        Employee employee = findEmployeeById(id);
        log.debug("Employee found: id={}, currentName='{}', currentRole='{}'",
                employee.getId(), employee.getName(), employee.getRoleTitle());

        log.debug("Validating new department with id: {}", request.getDepartmentId());
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(DEPARTMENT, request.getDepartmentId()));
        log.debug("New department resolved: id={}, name='{}'", department.getId(), department.getName());

        Employee reportingManager = resolveReportingManager(request.getReportingManagerId());

        employeeMapper.updateEntity(employee, request, department, reportingManager);
        Employee updated = employeeRepository.save(employee);

        log.info("Employee updated successfully: id={}, name='{}', department='{}', role='{}'",
                updated.getId(), updated.getName(), department.getName(), updated.getRoleTitle());
        return employeeMapper.toResponse(updated);
    }

    // ----------------------------------------------------------------
    // Read
    // ----------------------------------------------------------------

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        log.debug("Fetching employee by id: {}", id);
        Employee employee = findEmployeeById(id);
        log.debug("Employee found: id={}, name='{}', department='{}'",
                employee.getId(), employee.getName(),
                employee.getDepartment() != null ? employee.getDepartment().getName() : "N/A");
        return employeeMapper.toResponse(employee);
    }

    @Override
    public PagedResponse<EmployeeResponse> getAllEmployees(Pageable pageable) {
        log.debug("Fetching all employees — page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Employee> employees = employeeRepository.findAll(pageable);
        Page<EmployeeResponse> responsePage = employees.map(employeeMapper::toResponse);

        log.info("Fetched {} employees (page {}/{}, totalElements={})",
                responsePage.getNumberOfElements(),
                pageable.getPageNumber() + 1,
                responsePage.getTotalPages(),
                responsePage.getTotalElements());
        return PagedResponse.from(responsePage);
    }

    @Override
    public PagedResponse<EmployeeLookupResponse> getEmployeeLookup(Pageable pageable) {
        log.debug("Fetching employee lookup list — page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<EmployeeLookupResponse> responses = employeeRepository.findAllLookup(pageable);

        log.info("Fetched {} employee lookup entries (totalElements={})",
                responses.getNumberOfElements(), responses.getTotalElements());
        return PagedResponse.from(responses);
    }

    // ----------------------------------------------------------------
    // Patch Department
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public EmployeeResponse updateEmployeeDepartment(Long id, UpdateEmployeeDepartmentRequest request) {
        log.debug("Received request to update department for employee id={} — newDepartmentId={}",
                id, request.getDepartmentId());

        Employee employee = findEmployeeById(id);
        String oldDepartmentName = employee.getDepartment() != null
                ? employee.getDepartment().getName() : "N/A";
        log.debug("Employee found: id={}, name='{}', currentDepartment='{}'",
                employee.getId(), employee.getName(), oldDepartmentName);

        log.debug("Validating new department with id: {}", request.getDepartmentId());
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(DEPARTMENT, request.getDepartmentId()));

        employee.setDepartment(department);
        Employee updated = employeeRepository.save(employee);

        log.info("Employee department updated: id={}, name='{}', oldDepartment='{}', newDepartment='{}'",
                updated.getId(), updated.getName(), oldDepartmentName, department.getName());
        return employeeMapper.toResponse(updated);
    }

    // ----------------------------------------------------------------
    // Reporting Chain
    // ----------------------------------------------------------------

    @Override
    public List<EmployeeSummaryResponse> getReportingChain(Long id) {
        log.debug("Fetching reporting chain for employee id: {}", id);

        if (!employeeRepository.existsById(id)) {
            log.debug("Employee with id={} not found — throwing ResourceNotFoundException", id);
            throw new ResourceNotFoundException("Employee", id);
        }

        List<EmployeeRepository.ReportingChainProjection> chain =
                employeeRepository.findReportingChain(id);

        log.info("Reporting chain for employee id={}: {} level(s) up to top manager", id, chain.size());
        if (log.isDebugEnabled()) {
            chain.forEach(p -> log.debug("  Chain member: id={}, name='{}', role='{}'",
                    p.getId(), p.getName(), p.getRoleTitle()));
        }

        return chain.stream()
                .map(p -> EmployeeSummaryResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .roleTitle(p.getRoleTitle())
                        .build())
                .toList();
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.debug("Employee not found with id: {}", id);
                    return new ResourceNotFoundException("Employee", id);
                });
    }

    private Employee resolveReportingManager(Long reportingManagerId) {
        if (reportingManagerId == null) {
            log.debug("No reporting manager specified — top-level employee");
            return null;
        }
        log.debug("Validating reporting manager with id: {}", reportingManagerId);
        Employee manager = employeeRepository.findById(reportingManagerId)
                .orElseThrow(() -> {
                    log.debug("Reporting manager not found with id: {}", reportingManagerId);
                    return new ResourceNotFoundException("Reporting Manager (Employee)", reportingManagerId);
                });
        log.debug("Reporting manager resolved: id={}, name='{}'", manager.getId(), manager.getName());
        return manager;
    }
}
