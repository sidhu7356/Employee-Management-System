package com.company.ems.exception;

/**
 * Thrown when a business rule is violated (e.g., deleting a department with employees).
 * Maps to HTTP 422 Unprocessable Entity.
 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
