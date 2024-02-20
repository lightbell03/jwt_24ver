package com.example.jwt.service;

import com.example.jwt.dto.request.SignUpRequest;
import com.example.jwt.entity.User;
import com.example.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;

    public void createUser(SignUpRequest signUpRequest) {
        Optional<User> optionalUser = userRepository.findByUserId(signUpRequest.getUserId());

        if(optionalUser.isPresent()) {
            throw new RuntimeException("중복 아이디");
        }

        userRepository.save(User.builder()
                .userId(signUpRequest.getUserId())
                .password(signUpRequest.getPassword())
                .build());
    }

    @Transactional(readOnly = true)
    public void getUser() {

    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("user id not found"));
        return null;
    }
}
