package com.example.jwt.controller;

import com.example.jwt.dto.request.SignUpRequest;
import com.example.jwt.dto.response.UserResponse;
import com.example.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest signUpRequest) {
        log.info("회원가입 시작");
        userService.createUser(signUpRequest);
        log.info("회원가입 성공");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getAllUser());
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUser(@RequestHeader("X_USER_ID") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUser(id));
    }
}
