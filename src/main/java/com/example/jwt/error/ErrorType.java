package com.example.jwt.error;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    BAD_REQUEST("bad request", HttpStatus.BAD_REQUEST, 4000),
    UNAUTHORIZATION("unAuthorization", HttpStatus.UNAUTHORIZED, 4011),
    EXPIRED_TOKEN("token expired", HttpStatus.UNAUTHORIZED, 4012),
    INTERNAL_SERVER_ERROR("internal server error", HttpStatus.INTERNAL_SERVER_ERROR, 5000);
    ;

    private final String message;
    private final HttpStatus status;
    private final int errorCode;

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
