package com.example.jwt.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
        return http
                .csrf(config -> config.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/sign-up").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .anyRequest().authenticated())
                .build();
    }
}
