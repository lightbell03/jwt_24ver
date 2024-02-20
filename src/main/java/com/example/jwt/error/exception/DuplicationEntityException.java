package com.example.jwt.error.exception;

public class DuplicationEntityException extends RuntimeException {
    public DuplicationEntityException(String message) {
        super(message);
    }
}
