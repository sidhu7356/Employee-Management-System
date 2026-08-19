package com.company.ems.service.impl;

import com.company.ems.dto.request.CreateEmployeeRequest;
import com.company.ems.dto.request.UpdateEmployeeDepartmentRequest;
import com.company.ems.dto.request.UpdateEmployeeRequest;
import com.company.ems.dto.response.*;
import com.company.ems.entity.Department;
import com.company.ems.entity.Employee;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.mapper.EmployeeMapper;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeServiceImpl Unit Tests")
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    // ----------------------------------------------------------------
    // Test fixtures
    // ----------------------------------------------------------------
    private Department testDepartment;
    private Employee testManager;
    private Employee testEmployee;
    private EmployeeResponse testEmployeeResponse;
    private CreateEmployeeRequest createRequest;
    private UpdateEmployeeRequest updateRequest;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .id(1L)
                .name("Engineering")
                .creationDate(LocalDate.of(2020, 1, 15))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testManager = Employee.builder()
                .id(1L)
                .employeeCode("EMP-00001")
                .name("Alice Johnson")
                .roleTitle("CEO")
                .salary(BigDecimal.valueOf(180000))
                .department(testDepartment)
                .joiningDate(LocalDate.of(2015, 1, 1))
                .yearlyBonusPercentage(BigDecimal.valueOf(25))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testEmployee = Employee.builder()
                .id(2L)
                .employeeCode("EMP-ABCD1234")
                .name("Bob Williams")
                .roleTitle("VP of Engineering")
                .salary(BigDecimal.valueOf(140000))
                .department(testDepartment)
                .joiningDate(LocalDate.of(2015, 3, 15))
                .yearlyBonusPercentage(BigDecimal.valueOf(20))
                .reportingManager(testManager)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testEmployeeResponse = EmployeeResponse.builder()
                .id(2L)
                .employeeCode("EMP-ABCD1234")
                .name("Bob Williams")
                .roleTitle("VP of Engineering")
                .salary(BigDecimal.valueOf(140000))
                .department(new DepartmentSummaryResponse(1L, "Engineering"))
                .joiningDate(LocalDate.of(2015, 3, 15))
                .yearlyBonusPercentage(BigDecimal.valueOf(20))
                .reportingManager(new EmployeeSummaryResponse(1L, "Alice Johnson", "CEO"))
                .build();

        createRequest = CreateEmployeeRequest.builder()
                .name("Bob Williams")
                .dateOfBirth(LocalDate.of(1978, 7, 22))
                .salary(BigDecimal.valueOf(140000))
                .departmentId(1L)
                .roleTitle("VP of Engineering")
                .joiningDate(LocalDate.of(2015, 3, 15))
                .yearlyBonusPercentage(BigDecimal.valueOf(20))
                .reportingManagerId(1L)
                .build();

        updateRequest = UpdateEmployeeRequest.builder()
                .name("Bob Williams Updated")
                .dateOfBirth(LocalDate.of(1978, 7, 22))
                .salary(BigDecimal.valueOf(150000))
                .departmentId(1L)
                .roleTitle("Senior VP of Engineering")
                .joiningDate(LocalDate.of(2015, 3, 15))
                .yearlyBonusPercentage(BigDecimal.valueOf(22))
                .reportingManagerId(1L)
                .build();
    }

    // ================================================================
    // createEmployee tests
    // ================================================================
    @Nested
    @DisplayName("createEmployee()")
    class CreateEmployeeTests {

        @Test
        @DisplayName("should create employee successfully")
        void shouldCreateEmployeeSuccessfully() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testManager));
            when(employeeMapper.toEntity(createRequest, testDepartment, testManager)).thenReturn(testEmployee);
            when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
            when(employeeMapper.toResponse(testEmployee)).thenReturn(testEmployeeResponse);

            EmployeeResponse result = employeeService.createEmployee(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Bob Williams");
            assertThat(result.getSalary()).isEqualByComparingTo(BigDecimal.valueOf(140000));
            verify(employeeRepository).save(testEmployee);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when department not found")
        void shouldThrowWhenDepartmentNotFound() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.createEmployee(createRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Department")
                    .hasMessageContaining("1");

            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when reporting manager not found")
        void shouldThrowWhenReportingManagerNotFound() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.createEmployee(createRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("1");

            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("should create employee without reporting manager when reportingManagerId is null")
        void shouldCreateEmployeeWithoutReportingManager() {
            CreateEmployeeRequest requestWithoutManager = CreateEmployeeRequest.builder()
                    .name("Alice CEO")
                    .dateOfBirth(LocalDate.of(1975, 3, 15))
                    .salary(BigDecimal.valueOf(180000))
                    .departmentId(1L)
                    .roleTitle("CEO")
                    .joiningDate(LocalDate.of(2015, 1, 1))
                    .yearlyBonusPercentage(BigDecimal.valueOf(25))
                    .reportingManagerId(null)
                    .build();

            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(employeeMapper.toEntity(requestWithoutManager, testDepartment, null)).thenReturn(testManager);
            when(employeeRepository.save(testManager)).thenReturn(testManager);
            when(employeeMapper.toResponse(testManager)).thenReturn(testEmployeeResponse);

            EmployeeResponse result = employeeService.createEmployee(requestWithoutManager);

            assertThat(result).isNotNull();
            verify(employeeRepository, never()).findById(any()); // should not look up manager
            verify(employeeRepository).save(testManager);
        }
    }

    // ================================================================
    // updateEmployee tests
    // ================================================================
    @Nested
    @DisplayName("updateEmployee()")
    class UpdateEmployeeTests {

        @Test
        @DisplayName("should update employee successfully")
        void shouldUpdateEmployeeSuccessfully() {
            when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testManager));
            when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
            when(employeeMapper.toResponse(testEmployee)).thenReturn(testEmployeeResponse);

            EmployeeResponse result = employeeService.updateEmployee(2L, updateRequest);

            assertThat(result).isNotNull();
            verify(employeeMapper).updateEntity(testEmployee, updateRequest, testDepartment, testManager);
            verify(employeeRepository).save(testEmployee);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when employee not found")
        void shouldThrowWhenEmployeeNotFound() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.updateEmployee(999L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Employee")
                    .hasMessageContaining("999");
        }
    }

    // ================================================================
    // getEmployeeById tests
    // ================================================================
    @Nested
    @DisplayName("getEmployeeById()")
    class GetEmployeeByIdTests {

        @Test
        @DisplayName("should return employee when found")
        void shouldReturnEmployeeWhenFound() {
            when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));
            when(employeeMapper.toResponse(testEmployee)).thenReturn(testEmployeeResponse);

            EmployeeResponse result = employeeService.getEmployeeById(2L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getName()).isEqualTo("Bob Williams");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.getEmployeeById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Employee")
                    .hasMessageContaining("999");
        }
    }

    // ================================================================
    // getAllEmployees tests
    // ================================================================
    @Nested
    @DisplayName("getAllEmployees()")
    class GetAllEmployeesTests {

        @Test
        @DisplayName("should return paginated employee list")
        void shouldReturnPaginatedEmployees() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Employee> employeePage = new PageImpl<>(List.of(testEmployee), pageable, 1);

            when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
            when(employeeMapper.toResponse(testEmployee)).thenReturn(testEmployeeResponse);

            PagedResponse<EmployeeResponse> result = employeeService.getAllEmployees(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getPage()).isZero();
            assertThat(result.getSize()).isEqualTo(20);
        }
    }

    // ================================================================
    // getEmployeeLookup tests
    // ================================================================
    @Nested
    @DisplayName("getEmployeeLookup()")
    class GetEmployeeLookupTests {

        @Test
        @DisplayName("should return paginated lookup list")
        void shouldReturnPaginatedLookupList() {
            Pageable pageable = PageRequest.of(0, 20);
            EmployeeLookupResponse lookupResponse = new EmployeeLookupResponse(2L, "Bob Williams");
            Page<EmployeeLookupResponse> lookupPage = new PageImpl<>(List.of(lookupResponse), pageable, 1);

            when(employeeRepository.findAllLookup(pageable)).thenReturn(lookupPage);

            PagedResponse<EmployeeLookupResponse> result = employeeService.getEmployeeLookup(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).id()).isEqualTo(2L);
            assertThat(result.getContent().get(0).name()).isEqualTo("Bob Williams");
        }
    }

    // ================================================================
    // updateEmployeeDepartment tests
    // ================================================================
    @Nested
    @DisplayName("updateEmployeeDepartment()")
    class UpdateEmployeeDepartmentTests {

        @Test
        @DisplayName("should update department successfully")
        void shouldUpdateDepartmentSuccessfully() {
            Department newDept = Department.builder().id(2L).name("Finance").build();
            UpdateEmployeeDepartmentRequest request = new UpdateEmployeeDepartmentRequest(2L);

            when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));
            when(departmentRepository.findById(2L)).thenReturn(Optional.of(newDept));
            when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
            when(employeeMapper.toResponse(testEmployee)).thenReturn(testEmployeeResponse);

            EmployeeResponse result = employeeService.updateEmployeeDepartment(2L, request);

            assertThat(result).isNotNull();
            verify(employeeRepository).save(testEmployee);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when new department not found")
        void shouldThrowWhenNewDepartmentNotFound() {
            UpdateEmployeeDepartmentRequest request = new UpdateEmployeeDepartmentRequest(999L);

            when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee));
            when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.updateEmployeeDepartment(2L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Department")
                    .hasMessageContaining("999");
        }
    }
}
