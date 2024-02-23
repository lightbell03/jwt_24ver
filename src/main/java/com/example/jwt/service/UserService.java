package com.example.jwt.service;

import com.example.jwt.dto.request.SignUpRequest;
import com.example.jwt.dto.response.UserResponse;
import com.example.jwt.entity.User;
import com.example.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(SignUpRequest signUpRequest) {
        Optional<User> optionalUser = userRepository.findByUserId(signUpRequest.getUserId());

        if(optionalUser.isPresent()) {
            throw new RuntimeException("중복 아이디");
        }

        userRepository.save(User.builder()
                .userId(signUpRequest.getUserId())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .build());
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("not found user"));

        return new UserResponse(user.getId(), user.getUserId());
    }
}
