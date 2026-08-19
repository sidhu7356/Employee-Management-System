package com.company.ems.service.impl;

import com.company.ems.dto.request.CreateDepartmentRequest;
import com.company.ems.dto.request.UpdateDepartmentRequest;
import com.company.ems.dto.response.DepartmentAnalyticsResponse;
import com.company.ems.dto.response.DepartmentResponse;
import com.company.ems.dto.response.DepartmentWithEmployeesResponse;
import com.company.ems.dto.response.EmployeeResponse;
import com.company.ems.dto.response.PagedResponse;
import com.company.ems.entity.Department;
import com.company.ems.entity.Employee;
import com.company.ems.exception.BusinessRuleViolationException;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.mapper.DepartmentMapper;
import com.company.ems.mapper.EmployeeMapper;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeMapper employeeMapper;

    // ----------------------------------------------------------------
    // Create
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        log.debug("Received request to create department: name='{}', creationDate={}, departmentHeadId={}",
                request.getName(), request.getCreationDate(), request.getDepartmentHeadId());

        log.debug("Checking for duplicate department name: '{}'", request.getName());
        if (departmentRepository.existsByNameIgnoreCase(request.getName())) {
            log.debug("Duplicate department name found: '{}'", request.getName());
            throw new BusinessRuleViolationException(
                    "A department with the name '" + request.getName() + "' already exists.");
        }

        Employee departmentHead = resolveDepartmentHead(request.getDepartmentHeadId());
        Department department = departmentMapper.toEntity(request, departmentHead);
        Department saved = departmentRepository.save(department);

        log.info("Department created successfully: id={}, name='{}', head='{}'",
                saved.getId(), saved.getName(),
                saved.getDepartmentHead() != null ? saved.getDepartmentHead().getName() : "none");
        return departmentMapper.toResponse(saved);
    }

    // ----------------------------------------------------------------
    // Update
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request) {
        log.debug("Received request to update department: id={}, newName='{}', newDepartmentHeadId={}",
                id, request.getName(), request.getDepartmentHeadId());

        Department department = findDepartmentById(id);
        log.debug("Department found: id={}, currentName='{}', currentHead='{}'",
                department.getId(), department.getName(),
                department.getDepartmentHead() != null ? department.getDepartmentHead().getName() : "none");

        log.debug("Checking for duplicate name '{}' (excluding id={})", request.getName(), id);
        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            log.debug("Duplicate department name found: '{}'", request.getName());
            throw new BusinessRuleViolationException(
                    "A department with the name '" + request.getName() + "' already exists.");
        }

        Employee departmentHead = resolveDepartmentHead(request.getDepartmentHeadId());
        departmentMapper.updateEntity(department, request, departmentHead);
        Department updated = departmentRepository.save(department);

        log.info("Department updated successfully: id={}, name='{}', head='{}'",
                updated.getId(), updated.getName(),
                updated.getDepartmentHead() != null ? updated.getDepartmentHead().getName() : "none");
        return departmentMapper.toResponse(updated);
    }

    // ----------------------------------------------------------------
    // Delete
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        log.debug("Received request to delete department: id={}", id);

        Department department = findDepartmentById(id);
        log.debug("Department found: id={}, name='{}'", department.getId(), department.getName());

        log.debug("Checking if department id={} has assigned employees", id);
        if (employeeRepository.existsByDepartmentId(id)) {
            log.debug("Department id={} has employees assigned — deletion rejected", id);
            throw new BusinessRuleViolationException(
                    "Cannot delete department '" + department.getName() +
                    "' (id=" + id + ") because it has employees assigned to it. " +
                    "Please reassign or remove the employees first.");
        }

        departmentRepository.delete(department);
        log.info("Department deleted successfully: id={}, name='{}'", id, department.getName());
    }

    // ----------------------------------------------------------------
    // Read
    // ----------------------------------------------------------------

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        log.debug("Fetching department by id: {}", id);
        Department department = findDepartmentById(id);
        log.debug("Department found: id={}, name='{}', head='{}'",
                department.getId(), department.getName(),
                department.getDepartmentHead() != null ? department.getDepartmentHead().getName() : "none");
        return departmentMapper.toResponse(department);
    }

    @Override
    public PagedResponse<DepartmentResponse> getAllDepartments(Pageable pageable) {
        log.debug("Fetching all departments — page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Department> departments = departmentRepository.findAll(pageable);
        Page<DepartmentResponse> responsePage = departments.map(departmentMapper::toResponse);

        log.info("Fetched {} departments (page {}/{}, totalElements={})",
                responsePage.getNumberOfElements(),
                pageable.getPageNumber() + 1,
                responsePage.getTotalPages(),
                responsePage.getTotalElements());
        return PagedResponse.from(responsePage);
    }

    @Override
    public DepartmentWithEmployeesResponse getDepartmentWithEmployees(Long id, Pageable pageable) {
        log.debug("Fetching department id={} with employees — page={}, size={}",
                id, pageable.getPageNumber(), pageable.getPageSize());

        Department department = findDepartmentById(id);
        log.debug("Department found: id={}, name='{}'", department.getId(), department.getName());

        Page<Employee> employeePage = employeeRepository.findByDepartmentId(id, pageable);
        log.debug("Found {} employees in department '{}' (totalElements={})",
                employeePage.getNumberOfElements(), department.getName(), employeePage.getTotalElements());

        List<EmployeeResponse> employeeResponses = employeePage.getContent().stream()
                .map(employeeMapper::toResponse)
                .toList();

        log.info("Returning department '{}' (id={}) with {} employees on page {}/{}",
                department.getName(), id,
                employeePage.getNumberOfElements(),
                pageable.getPageNumber() + 1,
                employeePage.getTotalPages());

        return DepartmentWithEmployeesResponse.builder()
                .department(departmentMapper.toResponse(department))
                .employees(employeeResponses)
                .page(employeePage.getNumber())
                .size(employeePage.getSize())
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .build();
    }

    // ----------------------------------------------------------------
    // Analytics
    // ----------------------------------------------------------------

    @Override
    public DepartmentAnalyticsResponse getDepartmentAnalytics(Long id) {
        log.debug("Computing analytics for department id: {}", id);

        if (!departmentRepository.existsById(id)) {
            log.debug("Department not found with id: {} — throwing ResourceNotFoundException", id);
            throw new ResourceNotFoundException("Department", id);
        }

        long employeeCount = employeeRepository.countByDepartmentId(id);
        BigDecimal averageSalary = employeeRepository.findAverageSalaryByDepartmentId(id);
        BigDecimal totalSalary = employeeRepository.findTotalSalaryByDepartmentId(id);

        BigDecimal resolvedAvg = averageSalary != null
                ? averageSalary.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal resolvedTotal = totalSalary != null ? totalSalary : BigDecimal.ZERO;

        log.info("Analytics for department id={}: employeeCount={}, avgSalary={}, totalSalary={}",
                id, employeeCount, resolvedAvg, resolvedTotal);

        return DepartmentAnalyticsResponse.builder()
                .departmentId(id)
                .employeeCount(employeeCount)
                .averageSalary(resolvedAvg)
                .totalSalary(resolvedTotal)
                .build();
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.debug("Department not found with id: {}", id);
                    return new ResourceNotFoundException("Department", id);
                });
    }

    private Employee resolveDepartmentHead(Long departmentHeadId) {
        if (departmentHeadId == null) {
            log.debug("No department head specified");
            return null;
        }
        log.debug("Validating department head (employee) with id: {}", departmentHeadId);
        Employee head = employeeRepository.findById(departmentHeadId)
                .orElseThrow(() -> {
                    log.debug("Department head (employee) not found with id: {}", departmentHeadId);
                    return new ResourceNotFoundException("Department Head (Employee)", departmentHeadId);
                });
        log.debug("Department head resolved: id={}, name='{}', role='{}'",
                head.getId(), head.getName(), head.getRoleTitle());
        return head;
    }
}
