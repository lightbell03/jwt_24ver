package com.example.jwt.controller;

import com.example.jwt.dto.response.CommonResponse;
import com.example.jwt.error.ErrorType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CommonResponse<Void>> expiredJwtException() {
        return ResponseEntity.status(ErrorType.EXPIRED_TOKEN.getStatus())
                .body(new CommonResponse<>(ErrorType.EXPIRED_TOKEN.getErrorCode(), null, ErrorType.EXPIRED_TOKEN.getMessage()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<CommonResponse<Void>> jwtException() {
        return ResponseEntity.status(ErrorType.UNAUTHORIZATION.getStatus())
                .body(new CommonResponse<>(ErrorType.UNAUTHORIZATION.getErrorCode(), null, ErrorType.EXPIRED_TOKEN.getMessage()));
    }
}
