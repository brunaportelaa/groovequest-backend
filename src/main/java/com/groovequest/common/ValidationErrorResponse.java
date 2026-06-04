package com.groovequest.common;

import java.time.Instant;
import java.util.List;

public class ValidationErrorResponse {

    private String message;
    private int status;
    private Instant timestamp;
    private List<String> errors;

    public ValidationErrorResponse(
            String message,
            int status,
            Instant timestamp,
            List<String> errors
    ) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }
}