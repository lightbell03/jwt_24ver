package com.example.jwt.config.security;

import com.example.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private static final Set<AntPathRequestMatcher> requestMatcherSet = Set.of(new AntPathRequestMatcher("/auth/login"), new AntPathRequestMatcher("/auth/token/refresh"));
    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(matchPermitURL(request)) {

        } else {
            jwtService.validateToken()
        }

        filterChain.doFilter(request, response);
    }

    private boolean matchPermitURL(HttpServletRequest request) {
        for(AntPathRequestMatcher requestMatcher : requestMatcherSet) {
            if(requestMatcher.matches(request)) {
                return true;
            }
        }

        return false;
    }

    private String resolveBearerAuthorizeHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        String token = authorizationHeader.substring(6);

        return token;
    }
}
