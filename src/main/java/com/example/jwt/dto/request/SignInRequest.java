package com.example.jwt.dto.request;

import lombok.Getter;

@Getter
public class SignInRequest {
    private String userId;
    private String password;
}
