package com.example.jwt.service;

import com.example.jwt.dto.request.SignUpRequest;
import com.example.jwt.entity.User;
import com.example.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public void createUser(SignUpRequest signUpRequest) {
        userRepository.save(User.builder()
                .userId(signUpRequest.getUserId())
                .password(signUpRequest.getPassword())
                .build());
    }

    @Transactional(readOnly = true)
    public void getUser() {
    }
}
