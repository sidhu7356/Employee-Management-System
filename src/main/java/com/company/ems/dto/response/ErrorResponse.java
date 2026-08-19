package com.company.ems.dto.response;

import lombok.*;

/**
 * Standard error response body returned by the global exception handler.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
