package com.company.ems.repository;

import com.company.ems.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Check for duplicate department names (case-insensitive).
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Check for duplicate department names, excluding the current department (used during update).
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
