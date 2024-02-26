package com.example.jwt.controller;

import com.example.jwt.error.ErrorResponse;
import com.example.jwt.error.ErrorType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> expiredJwtException() {
        return ResponseEntity.status(ErrorType.EXPIRED_TOKEN.getStatus())
                .body(new ErrorResponse(ErrorType.EXPIRED_TOKEN.getMessage(), ErrorType.EXPIRED_TOKEN.getErrorCode()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> jwtException() {
        return ResponseEntity.status(ErrorType.UNAUTHORIZATION.getStatus())
                .body(new ErrorResponse(ErrorType.UNAUTHORIZATION.getMessage(), ErrorType.EXPIRED_TOKEN.getErrorCode()));
    }
}
