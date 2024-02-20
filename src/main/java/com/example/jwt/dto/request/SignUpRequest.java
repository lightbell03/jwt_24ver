package com.example.jwt.dto.request;

import lombok.Getter;

@Getter
public class SignUpRequest {
    private String userId;
    private String password;
}
