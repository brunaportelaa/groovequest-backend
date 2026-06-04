package com.groovequest.common;

import java.time.Instant;

public class ErrorResponse {

    private String message;
    private int status;
    private Instant timestamp;

    public ErrorResponse(String message, int status, Instant timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public static ErrorResponse of(String message, int status) {
        return new ErrorResponse(message, status, Instant.now());
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
}