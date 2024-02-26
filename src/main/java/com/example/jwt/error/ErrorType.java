package com.example.jwt.error;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    UNAUTHORIZATION("unAuthorization", HttpStatus.UNAUTHORIZED, 4011),
    EXPIRED_TOKEN("token expired", HttpStatus.UNAUTHORIZED, 4012),
    INTERNAL_SERVER_ERROR("internal server error", HttpStatus.INTERNAL_SERVER_ERROR, 5000);
    ;

    private String message;
    private HttpStatus status;
    private int errorCode;

    ErrorType(String message, HttpStatus status, int errorCode) {
        this.message = message;
        this.status = status;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
    }
}