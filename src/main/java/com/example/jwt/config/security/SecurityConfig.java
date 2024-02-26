package com.example.jwt.config.security;

import com.example.jwt.repository.RedisAuthRepository;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final RedisAuthRepository redisBlackListRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtService jwtService, RedisAuthRepository redisBlackListRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.redisBlackListRepository = redisBlackListRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
        return http
                .csrf(config -> config.disable())
                .httpBasic(config -> config.disable())
                .sessionManagement(config -> config.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/users/sign-up").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtFilter(jwtService, redisBlackListRepository, userRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ExceptionFilter(objectMapper), JwtFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
