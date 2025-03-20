package be.pxl.demo.api.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiError {
    private final LocalDateTime timeStamp;
    private String error;
    private HttpStatus status;

    public ApiError() {
        this.timeStamp = LocalDateTime.now();
    }

    public ApiError(String error, HttpStatus statusCode) {
        this();
        this.error = error;
        this.status = statusCode;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getError() {
        return error;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
