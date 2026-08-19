package com.company.ems.repository;

import com.company.ems.dto.response.EmployeeLookupResponse;
import com.company.ems.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Checks whether any employee is assigned to the given department.
     * Used to enforce the deletion guard on departments.
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e WHERE e.department.id = :departmentId")
    boolean existsByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * Paginated list of all employees belonging to a specific department.
     */
    @Query(
            value = "SELECT e FROM Employee e WHERE e.department.id = :departmentId",
            countQuery = "SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId"
    )
    Page<Employee> findByDepartmentId(@Param("departmentId") Long departmentId, Pageable pageable);

    /**
     * Lookup query: returns only id and name using JPQL constructor expression.
     * Avoids loading the full entity for lightweight lookup use cases.
     */
    @Query("SELECT new com.company.ems.dto.response.EmployeeLookupResponse(e.id, e.name) FROM Employee e")
    Page<EmployeeLookupResponse> findAllLookup(Pageable pageable);

    /**
     * Counts employees in a department — used for analytics.
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    long countByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * Average salary of employees in a department — used for analytics.
     */
    @Query("SELECT AVG(e.salary) FROM Employee e WHERE e.department.id = :departmentId")
    BigDecimal findAverageSalaryByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * Total salary of employees in a department — used for analytics.
     */
    @Query("SELECT SUM(e.salary) FROM Employee e WHERE e.department.id = :departmentId")
    BigDecimal findTotalSalaryByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * Recursive CTE to traverse the full reporting chain from an employee up to the CEO.
     * Uses PostgreSQL's WITH RECURSIVE for a single-query solution that avoids N+1 queries.
     */
    @Query(
            value = """
                    WITH RECURSIVE reporting_chain AS (
                        SELECT id, name, role_title, reporting_manager_id, 0 AS level
                        FROM ems.employee
                        WHERE id = :employeeId
                        UNION ALL
                        SELECT e.id, e.name, e.role_title, e.reporting_manager_id, rc.level + 1
                        FROM ems.employee e
                        INNER JOIN reporting_chain rc ON e.id = rc.reporting_manager_id
                    )
                    SELECT id, name, role_title
                    FROM reporting_chain
                    ORDER BY level
                    """,
            nativeQuery = true
    )
    List<ReportingChainProjection> findReportingChain(@Param("employeeId") Long employeeId);

    /**
     * Interface projection for the reporting chain native query result.
     */
    interface ReportingChainProjection {
        Long getId();
        String getName();
        String getRole_title();
    }
}
