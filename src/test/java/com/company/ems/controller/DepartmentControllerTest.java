package com.company.ems.controller;

import com.company.ems.dto.request.CreateDepartmentRequest;
import com.company.ems.dto.request.UpdateDepartmentRequest;
import com.company.ems.dto.response.*;
import com.company.ems.exception.BusinessRuleViolationException;
import com.company.ems.exception.GlobalExceptionHandler;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.service.DepartmentService;
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

@WebMvcTest(DepartmentController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("DepartmentController Integration Tests")
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    private DepartmentResponse sampleDepartmentResponse() {
        return DepartmentResponse.builder()
                .id(1L)
                .name("Engineering")
                .creationDate(LocalDate.of(2020, 1, 1))
                .departmentHead(new EmployeeSummaryResponse(1L, "Alice Chief", "VP of Engineering"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("POST /api/v1/departments - Create Department")
    class CreateDepartmentTests {

        @Test
        @DisplayName("Should create department successfully and return 201 Created")
        void createDepartment_success() throws Exception {
            CreateDepartmentRequest request = CreateDepartmentRequest.builder()
                    .name("Engineering")
                    .creationDate(LocalDate.of(2020, 1, 1))
                    .departmentHeadId(1L)
                    .build();

            DepartmentResponse response = sampleDepartmentResponse();
            when(departmentService.createDepartment(any(CreateDepartmentRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/v1/departments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Engineering")))
                    .andExpect(jsonPath("$.departmentHead.name", is("Alice Chief")));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when validation fails")
        void createDepartment_validationFailure() throws Exception {
            CreateDepartmentRequest request = CreateDepartmentRequest.builder()
                    .name("") // Blank name violates @NotBlank
                    .creationDate(null) // Null creationDate violates @NotNull
                    .build();

            mockMvc.perform(post("/api/v1/departments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Bad Request")));

            verify(departmentService, never()).createDepartment(any());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/departments/{id} - Update Department")
    class UpdateDepartmentTests {

        @Test
        @DisplayName("Should update department successfully and return 200 OK")
        void updateDepartment_success() throws Exception {
            UpdateDepartmentRequest request = UpdateDepartmentRequest.builder()
                    .name("Engineering & Technology")
                    .creationDate(LocalDate.of(2020, 1, 1))
                    .departmentHeadId(1L)
                    .build();

            DepartmentResponse response = sampleDepartmentResponse();
            response.setName("Engineering & Technology");

            when(departmentService.updateDepartment(eq(1L), any(UpdateDepartmentRequest.class))).thenReturn(response);

            mockMvc.perform(put("/api/v1/departments/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Engineering & Technology")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when department to update does not exist")
        void updateDepartment_notFound() throws Exception {
            UpdateDepartmentRequest request = UpdateDepartmentRequest.builder()
                    .name("Engineering")
                    .creationDate(LocalDate.of(2020, 1, 1))
                    .build();

            when(departmentService.updateDepartment(eq(999L), any(UpdateDepartmentRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Department not found with id: 999"));

            mockMvc.perform(put("/api/v1/departments/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", containsString("Department not found with id: 999")));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/departments/{id} - Delete Department")
    class DeleteDepartmentTests {

        @Test
        @DisplayName("Should delete department successfully and return 204 No Content")
        void deleteDepartment_success() throws Exception {
            doNothing().when(departmentService).deleteDepartment(1L);

            mockMvc.perform(delete("/api/v1/departments/1"))
                    .andExpect(status().isNoContent());

            verify(departmentService, times(1)).deleteDepartment(1L);
        }

        @Test
        @DisplayName("Should return 422 Unprocessable Entity when department has assigned employees")
        void deleteDepartment_businessRuleViolation() throws Exception {
            doThrow(new BusinessRuleViolationException("Cannot delete department because 5 employees are assigned to it"))
                    .when(departmentService).deleteDepartment(1L);

            mockMvc.perform(delete("/api/v1/departments/1"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.status", is(422)))
                    .andExpect(jsonPath("$.message", containsString("Cannot delete department")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/departments/{id} - Get Department by ID")
    class GetDepartmentByIdTests {

        @Test
        @DisplayName("Should return department details without expansion")
        void getDepartmentById_standard() throws Exception {
            DepartmentResponse response = sampleDepartmentResponse();
            when(departmentService.getDepartmentById(1L)).thenReturn(response);

            mockMvc.perform(get("/api/v1/departments/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Engineering")));
        }

        @Test
        @DisplayName("Should return department with employee list when expand=employee")
        void getDepartmentById_expandedEmployees() throws Exception {
            EmployeeResponse emp = EmployeeResponse.builder()
                    .id(10L)
                    .employeeCode("EMP-00010")
                    .name("Jane Smith")
                    .roleTitle("Software Engineer")
                    .build();

            DepartmentWithEmployeesResponse expandedResponse = DepartmentWithEmployeesResponse.builder()
                    .department(sampleDepartmentResponse())
                    .employees(List.of(emp))
                    .page(0)
                    .size(20)
                    .totalElements(1)
                    .totalPages(1)
                    .build();

            when(departmentService.getDepartmentWithEmployees(eq(1L), any(Pageable.class)))
                    .thenReturn(expandedResponse);

            mockMvc.perform(get("/api/v1/departments/1?expand=employee"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.department.id", is(1)))
                    .andExpect(jsonPath("$.employees", hasSize(1)))
                    .andExpect(jsonPath("$.employees[0].name", is("Jane Smith")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when department ID does not exist")
        void getDepartmentById_notFound() throws Exception {
            when(departmentService.getDepartmentById(999L))
                    .thenThrow(new ResourceNotFoundException("Department not found with id: 999"));

            mockMvc.perform(get("/api/v1/departments/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Department not found with id: 999")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/departments - Get All Departments")
    class GetAllDepartmentsTests {

        @Test
        @DisplayName("Should return paginated department list")
        void getAllDepartments_success() throws Exception {
            PagedResponse<DepartmentResponse> pagedResponse = PagedResponse.<DepartmentResponse>builder()
                    .content(List.of(sampleDepartmentResponse()))
                    .page(0)
                    .size(20)
                    .totalElements(1)
                    .totalPages(1)
                    .build();

            when(departmentService.getAllDepartments(any(Pageable.class))).thenReturn(pagedResponse);

            mockMvc.perform(get("/api/v1/departments?page=0&size=20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].name", is("Engineering")));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when an invalid sort property is provided")
        void getAllDepartments_invalidSortProperty() throws Exception {
            PropertyReferenceException exception = new PropertyReferenceException(
                    "invalidField", TypeInformation.OBJECT, List.of()
            );

            when(departmentService.getAllDepartments(any(Pageable.class))).thenThrow(exception);

            mockMvc.perform(get("/api/v1/departments?sort=invalidField,asc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.message", containsString("Invalid sort property")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/departments/{id}/analytics - Get Department Analytics")
    class GetDepartmentAnalyticsTests {

        @Test
        @DisplayName("Should return department analytics data")
        void getDepartmentAnalytics_success() throws Exception {
            DepartmentAnalyticsResponse analytics = DepartmentAnalyticsResponse.builder()
                    .departmentId(1L)
                    .employeeCount(15)
                    .averageSalary(new BigDecimal("95000.00"))
                    .totalSalary(new BigDecimal("1425000.00"))
                    .build();

            when(departmentService.getDepartmentAnalytics(1L)).thenReturn(analytics);

            mockMvc.perform(get("/api/v1/departments/1/analytics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.departmentId", is(1)))
                    .andExpect(jsonPath("$.employeeCount", is(15)))
                    .andExpect(jsonPath("$.averageSalary", is(95000.00)))
                    .andExpect(jsonPath("$.totalSalary", is(1425000.00)));
        }
    }
}
