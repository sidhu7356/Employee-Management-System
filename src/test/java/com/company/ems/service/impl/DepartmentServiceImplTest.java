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
@DisplayName("DepartmentServiceImpl Unit Tests")
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    // ----------------------------------------------------------------
    // Test fixtures
    // ----------------------------------------------------------------
    private Department testDepartment;
    private Employee testHead;
    private DepartmentResponse testDepartmentResponse;
    private CreateDepartmentRequest createRequest;
    private UpdateDepartmentRequest updateRequest;

    @BeforeEach
    void setUp() {
        testHead = Employee.builder()
                .id(1L)
                .name("Bob Williams")
                .roleTitle("VP of Engineering")
                .salary(BigDecimal.valueOf(140000))
                .joiningDate(LocalDate.of(2015, 3, 15))
                .yearlyBonusPercentage(BigDecimal.valueOf(20))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testDepartment = Department.builder()
                .id(1L)
                .name("Engineering")
                .creationDate(LocalDate.of(2020, 1, 15))
                .departmentHead(testHead)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testDepartmentResponse = DepartmentResponse.builder()
                .id(1L)
                .name("Engineering")
                .creationDate(LocalDate.of(2020, 1, 15))
                .build();

        createRequest = CreateDepartmentRequest.builder()
                .name("Engineering")
                .creationDate(LocalDate.of(2020, 1, 15))
                .departmentHeadId(1L)
                .build();

        updateRequest = UpdateDepartmentRequest.builder()
                .name("Engineering Updated")
                .creationDate(LocalDate.of(2020, 1, 15))
                .departmentHeadId(1L)
                .build();
    }

    // ================================================================
    // createDepartment tests
    // ================================================================
    @Nested
    @DisplayName("createDepartment()")
    class CreateDepartmentTests {

        @Test
        @DisplayName("should create department successfully")
        void shouldCreateDepartmentSuccessfully() {
            when(departmentRepository.existsByNameIgnoreCase("Engineering")).thenReturn(false);
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testHead));
            when(departmentMapper.toEntity(createRequest, testHead)).thenReturn(testDepartment);
            when(departmentRepository.save(testDepartment)).thenReturn(testDepartment);
            when(departmentMapper.toResponse(testDepartment)).thenReturn(testDepartmentResponse);

            DepartmentResponse result = departmentService.createDepartment(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Engineering");
            verify(departmentRepository).save(testDepartment);
        }

        @Test
        @DisplayName("should throw BusinessRuleViolationException when name already exists")
        void shouldThrowWhenNameAlreadyExists() {
            when(departmentRepository.existsByNameIgnoreCase("Engineering")).thenReturn(true);

            assertThatThrownBy(() -> departmentService.createDepartment(createRequest))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("Engineering");

            verify(departmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when department head not found")
        void shouldThrowWhenDepartmentHeadNotFound() {
            when(departmentRepository.existsByNameIgnoreCase("Engineering")).thenReturn(false);
            when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> departmentService.createDepartment(createRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("1");
        }

        @Test
        @DisplayName("should create department without head when departmentHeadId is null")
        void shouldCreateDepartmentWithoutHead() {
            CreateDepartmentRequest requestWithoutHead = CreateDepartmentRequest.builder()
                    .name("New Department")
                    .creationDate(LocalDate.of(2024, 1, 1))
                    .departmentHeadId(null)
                    .build();

            when(departmentRepository.existsByNameIgnoreCase("New Department")).thenReturn(false);
            when(departmentMapper.toEntity(requestWithoutHead, null)).thenReturn(testDepartment);
            when(departmentRepository.save(testDepartment)).thenReturn(testDepartment);
            when(departmentMapper.toResponse(testDepartment)).thenReturn(testDepartmentResponse);

            DepartmentResponse result = departmentService.createDepartment(requestWithoutHead);

            assertThat(result).isNotNull();
            verify(employeeRepository, never()).findById(any());
        }
    }

    // ================================================================
    // updateDepartment tests
    // ================================================================
    @Nested
    @DisplayName("updateDepartment()")
    class UpdateDepartmentTests {

        @Test
        @DisplayName("should update department successfully")
        void shouldUpdateDepartmentSuccessfully() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(departmentRepository.existsByNameIgnoreCaseAndIdNot("Engineering Updated", 1L)).thenReturn(false);
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testHead));
            when(departmentRepository.save(testDepartment)).thenReturn(testDepartment);
            when(departmentMapper.toResponse(testDepartment)).thenReturn(testDepartmentResponse);

            DepartmentResponse result = departmentService.updateDepartment(1L, updateRequest);

            assertThat(result).isNotNull();
            verify(departmentMapper).updateEntity(testDepartment, updateRequest, testHead);
            verify(departmentRepository).save(testDepartment);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when department not found")
        void shouldThrowWhenDepartmentNotFound() {
            when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> departmentService.updateDepartment(999L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Department")
                    .hasMessageContaining("999");
        }
    }

    // ================================================================
    // deleteDepartment tests
    // ================================================================
    @Nested
    @DisplayName("deleteDepartment()")
    class DeleteDepartmentTests {

        @Test
        @DisplayName("should delete department successfully when no employees assigned")
        void shouldDeleteDepartmentSuccessfully() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(employeeRepository.existsByDepartmentId(1L)).thenReturn(false);

            departmentService.deleteDepartment(1L);

            verify(departmentRepository).delete(testDepartment);
        }

        @Test
        @DisplayName("should throw BusinessRuleViolationException when employees are assigned")
        void shouldThrowWhenDepartmentHasEmployees() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(employeeRepository.existsByDepartmentId(1L)).thenReturn(true);

            assertThatThrownBy(() -> departmentService.deleteDepartment(1L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("employees assigned");

            verify(departmentRepository, never()).delete(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when department not found")
        void shouldThrowWhenDepartmentNotFound() {
            when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> departmentService.deleteDepartment(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Department")
                    .hasMessageContaining("999");

            verify(departmentRepository, never()).delete(any());
        }
    }

    // ================================================================
    // getDepartmentById tests
    // ================================================================
    @Nested
    @DisplayName("getDepartmentById()")
    class GetDepartmentByIdTests {

        @Test
        @DisplayName("should return department when found")
        void shouldReturnDepartmentWhenFound() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(departmentMapper.toResponse(testDepartment)).thenReturn(testDepartmentResponse);

            DepartmentResponse result = departmentService.getDepartmentById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> departmentService.getDepartmentById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    // ================================================================
    // getAllDepartments tests
    // ================================================================
    @Nested
    @DisplayName("getAllDepartments()")
    class GetAllDepartmentsTests {

        @Test
        @DisplayName("should return paginated department list")
        void shouldReturnPaginatedDepartments() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Department> page = new PageImpl<>(List.of(testDepartment), pageable, 1);

            when(departmentRepository.findAll(pageable)).thenReturn(page);
            when(departmentMapper.toResponse(testDepartment)).thenReturn(testDepartmentResponse);

            PagedResponse<DepartmentResponse> result = departmentService.getAllDepartments(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getPage()).isZero();
        }
    }

    // ================================================================
    // getDepartmentAnalytics tests
    // ================================================================
    @Nested
    @DisplayName("getDepartmentAnalytics()")
    class GetDepartmentAnalyticsTests {

        @Test
        @DisplayName("should return analytics for existing department")
        void shouldReturnAnalytics() {
            when(departmentRepository.existsById(1L)).thenReturn(true);
            when(employeeRepository.countByDepartmentId(1L)).thenReturn(10L);
            when(employeeRepository.findAverageSalaryByDepartmentId(1L)).thenReturn(BigDecimal.valueOf(95000));
            when(employeeRepository.findTotalSalaryByDepartmentId(1L)).thenReturn(BigDecimal.valueOf(950000));

            DepartmentAnalyticsResponse result = departmentService.getDepartmentAnalytics(1L);

            assertThat(result).isNotNull();
            assertThat(result.getDepartmentId()).isEqualTo(1L);
            assertThat(result.getEmployeeCount()).isEqualTo(10L);
            assertThat(result.getAverageSalary()).isEqualByComparingTo(BigDecimal.valueOf(95000));
            assertThat(result.getTotalSalary()).isEqualByComparingTo(BigDecimal.valueOf(950000));
        }

        @Test
        @DisplayName("should return zero values when department has no employees")
        void shouldReturnZeroValuesWhenEmpty() {
            when(departmentRepository.existsById(1L)).thenReturn(true);
            when(employeeRepository.countByDepartmentId(1L)).thenReturn(0L);
            when(employeeRepository.findAverageSalaryByDepartmentId(1L)).thenReturn(null);
            when(employeeRepository.findTotalSalaryByDepartmentId(1L)).thenReturn(null);

            DepartmentAnalyticsResponse result = departmentService.getDepartmentAnalytics(1L);

            assertThat(result.getEmployeeCount()).isZero();
            assertThat(result.getAverageSalary()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.getTotalSalary()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when department not found")
        void shouldThrowWhenDepartmentNotFound() {
            when(departmentRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> departmentService.getDepartmentAnalytics(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }
}
