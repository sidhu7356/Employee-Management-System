package com.company.ems.exception;

/**
 * Thrown when a requested resource (Employee, Department) is not found in the database.
 * Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s with id '%d' was not found", resourceName, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
