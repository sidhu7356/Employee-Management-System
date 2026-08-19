package com.company.ems.dto.response;

/**
 * Lightweight employee projection for lookup endpoints.
 * Returns only id and name.
 *
 * @param id   the employee's primary key
 * @param name the employee's full name
 */
public record EmployeeLookupResponse(Long id, String name) {
}
