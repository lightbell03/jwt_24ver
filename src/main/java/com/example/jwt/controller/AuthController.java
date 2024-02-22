package com.example.jwt.controller;

import com.example.jwt.dto.request.SignInRequest;
import com.example.jwt.dto.response.TokenResponse;
import com.example.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody SignInRequest signInRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.login(signInRequest));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<Void> refreshToken() {
        return ResponseEntity.ok().build();
    }
}
