package com.example.jwt.controller;

import com.example.jwt.dto.request.SignUpRequest;
import com.example.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest signUpRequest) {
        log.info("회원가입 시작");
        authService.createUser(signUpRequest);
        log.info("회원가입 성공");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login() {

        return ResponseEntity.ok().build();
    }
}
