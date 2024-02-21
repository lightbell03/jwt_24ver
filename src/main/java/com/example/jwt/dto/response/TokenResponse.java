package com.example.jwt.dto.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;
}
