package com.example.jwt.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonResponse<T> {
    private final int code;
    private final T data;
    private final String message;
}
