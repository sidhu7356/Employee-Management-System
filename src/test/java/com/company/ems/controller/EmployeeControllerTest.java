package com.company.ems.controller;

import com.company.ems.dto.request.CreateEmployeeRequest;
import com.company.ems.dto.request.UpdateEmployeeDepartmentRequest;
import com.company.ems.dto.request.UpdateEmployeeRequest;
import com.company.ems.dto.response.*;
import com.company.ems.exception.GlobalExceptionHandler;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("EmployeeController Integration Tests")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeResponse sampleEmployeeResponse() {
        return EmployeeResponse.builder()
                .id(1L)
                .employeeCode("EMP-00001")
                .name("John Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .salary(new BigDecimal("85000.00"))
                .department(new DepartmentSummaryResponse(1L, "Engineering"))
                .address("123 Main St")
                .roleTitle("Software Engineer")
                .joiningDate(LocalDate.of(2022, 5, 10))
                .yearlyBonusPercentage(new BigDecimal("10.00"))
                .reportingManager(new EmployeeSummaryResponse(2L, "Jane Manager", "Engineering Manager"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("POST /api/v1/employees - Create Employee")
    class CreateEmployeeTests {

        @Test
        @DisplayName("Should create employee successfully and return 201 Created")
        void createEmployee_success() throws Exception {
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .salary(new BigDecimal("85000.00"))
                    .departmentId(1L)
                    .address("123 Main St")
                    .roleTitle("Software Engineer")
                    .joiningDate(LocalDate.of(2022, 5, 10))
                    .yearlyBonusPercentage(new BigDecimal("10.00"))
                    .reportingManagerId(2L)
                    .build();

            EmployeeResponse response = sampleEmployeeResponse();
            when(employeeService.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.employeeCode", is("EMP-00001")))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.salary", is(85000.00)))
                    .andExpect(jsonPath("$.department.name", is("Engineering")));

            verify(employeeService, times(1)).createEmployee(any(CreateEmployeeRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when validation fails")
        void createEmployee_validationFailure() throws Exception {
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("") // Blank name violates @NotBlank
                    .salary(new BigDecimal("-100")) // Negative salary violates @DecimalMin
                    .build();

            mockMvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Bad Request")));

            verify(employeeService, never()).createEmployee(any());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/employees/{id} - Update Employee")
    class UpdateEmployeeTests {

        @Test
        @DisplayName("Should update employee successfully and return 200 OK")
        void updateEmployee_success() throws Exception {
            UpdateEmployeeRequest request = UpdateEmployeeRequest.builder()
                    .name("John Doe Updated")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .salary(new BigDecimal("95000.00"))
                    .departmentId(1L)
                    .address("456 New St")
                    .roleTitle("Senior Software Engineer")
                    .joiningDate(LocalDate.of(2022, 5, 10))
                    .yearlyBonusPercentage(new BigDecimal("15.00"))
                    .build();

            EmployeeResponse response = sampleEmployeeResponse();
            response.setName("John Doe Updated");
            response.setSalary(new BigDecimal("95000.00"));
            response.setRoleTitle("Senior Software Engineer");

            when(employeeService.updateEmployee(eq(1L), any(UpdateEmployeeRequest.class))).thenReturn(response);

            mockMvc.perform(put("/api/v1/employees/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe Updated")))
                    .andExpect(jsonPath("$.salary", is(95000.00)))
                    .andExpect(jsonPath("$.roleTitle", is("Senior Software Engineer")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when employee to update does not exist")
        void updateEmployee_notFound() throws Exception {
            UpdateEmployeeRequest request = UpdateEmployeeRequest.builder()
                    .name("John Doe")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .salary(new BigDecimal("95000.00"))
                    .departmentId(1L)
                    .roleTitle("Senior Software Engineer")
                    .joiningDate(LocalDate.of(2022, 5, 10))
                    .yearlyBonusPercentage(new BigDecimal("15.00"))
                    .build();

            when(employeeService.updateEmployee(eq(999L), any(UpdateEmployeeRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Employee not found with id: 999"));

            mockMvc.perform(put("/api/v1/employees/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", containsString("Employee not found with id: 999")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/employees/{id} - Get Employee by ID")
    class GetEmployeeByIdTests {

        @Test
        @DisplayName("Should return employee details when found")
        void getEmployeeById_success() throws Exception {
            EmployeeResponse response = sampleEmployeeResponse();
            when(employeeService.getEmployeeById(1L)).thenReturn(response);

            mockMvc.perform(get("/api/v1/employees/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.employeeCode", is("EMP-00001")))
                    .andExpect(jsonPath("$.name", is("John Doe")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when employee ID does not exist")
        void getEmployeeById_notFound() throws Exception {
            when(employeeService.getEmployeeById(999L))
                    .thenThrow(new ResourceNotFoundException("Employee not found with id: 999"));

            mockMvc.perform(get("/api/v1/employees/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Employee not found with id: 999")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/employees - Get All Employees")
    class GetAllEmployeesTests {

        @Test
        @DisplayName("Should return full employee list when lookup=false")
        void getAllEmployees_fullDetails() throws Exception {
            PagedResponse<EmployeeResponse> pagedResponse = PagedResponse.<EmployeeResponse>builder()
                    .content(List.of(sampleEmployeeResponse()))
                    .page(0)
                    .size(20)
                    .totalElements(1)
                    .totalPages(1)
                    .build();

            when(employeeService.getAllEmployees(any(Pageable.class))).thenReturn(pagedResponse);

            mockMvc.perform(get("/api/v1/employees?page=0&size=20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].name", is("John Doe")))
                    .andExpect(jsonPath("$.page", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)));
        }

        @Test
        @DisplayName("Should return lightweight lookup list when lookup=true")
        void getAllEmployees_lookupMode() throws Exception {
            PagedResponse<EmployeeLookupResponse> pagedResponse = PagedResponse.<EmployeeLookupResponse>builder()
                    .content(List.of(new EmployeeLookupResponse(1L, "John Doe")))
                    .page(0)
                    .size(20)
                    .totalElements(1)
                    .totalPages(1)
                    .build();

            when(employeeService.getEmployeeLookup(any(Pageable.class))).thenReturn(pagedResponse);

            mockMvc.perform(get("/api/v1/employees?lookup=true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id", is(1)))
                    .andExpect(jsonPath("$.content[0].name", is("John Doe")));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when an invalid sort property is specified")
        void getAllEmployees_invalidSortProperty() throws Exception {
            PropertyReferenceException exception = new PropertyReferenceException(
                    "invalidProperty", TypeInformation.OBJECT, List.of()
            );

            when(employeeService.getAllEmployees(any(Pageable.class))).thenThrow(exception);

            mockMvc.perform(get("/api/v1/employees?sort=invalidProperty,asc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.message", containsString("Invalid sort property")));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/employees/{id}/department - Update Employee Department")
    class UpdateEmployeeDepartmentTests {

        @Test
        @DisplayName("Should update employee department successfully")
        void updateEmployeeDepartment_success() throws Exception {
            UpdateEmployeeDepartmentRequest request = new UpdateEmployeeDepartmentRequest(2L);
            EmployeeResponse response = sampleEmployeeResponse();
            response.setDepartment(new DepartmentSummaryResponse(2L, "Product Management"));

            when(employeeService.updateEmployeeDepartment(eq(1L), any(UpdateEmployeeDepartmentRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(patch("/api/v1/employees/1/department")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.department.id", is(2)))
                    .andExpect(jsonPath("$.department.name", is("Product Management")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/employees/{id}/reporting-chain - Get Reporting Chain")
    class GetReportingChainTests {

        @Test
        @DisplayName("Should return hierarchy list for reporting chain")
        void getReportingChain_success() throws Exception {
            List<EmployeeSummaryResponse> chain = List.of(
                    new EmployeeSummaryResponse(1L, "John Doe", "Software Engineer"),
                    new EmployeeSummaryResponse(2L, "Jane Manager", "Engineering Manager")
            );

            when(employeeService.getReportingChain(1L)).thenReturn(chain);

            mockMvc.perform(get("/api/v1/employees/1/reporting-chain"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("John Doe")))
                    .andExpect(jsonPath("$[1].name", is("Jane Manager")));
        }
    }
}
