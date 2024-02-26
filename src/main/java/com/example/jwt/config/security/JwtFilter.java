package com.example.jwt.config.security;

import com.example.jwt.entity.User;
import com.example.jwt.repository.RedisAuthRepository;
import com.example.jwt.repository.RedisBlackListRepository;
import com.example.jwt.repository.RedisTokenRepository;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private static final Set<AntPathRequestMatcher> requestMatcherSet =
            Set.of(new AntPathRequestMatcher("/auth/login"), new AntPathRequestMatcher("/auth/token/refresh"), new AntPathRequestMatcher("/users/sign-up"));
    private static final String ACCESS_TOKEN_PREFIX = "ACCESS_TOKEN:";
    private final JwtService jwtService;
    private final RedisAuthRepository redisBlackListRepository;
    private final UserRepository userRepository;

    public JwtFilter(JwtService jwtService, RedisAuthRepository redisBlackListRepository, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.redisBlackListRepository = redisBlackListRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!matchPermitURL(request)) {
            try {
                String token = resolveBearerAuthorizeHeader(request);
                Long userId = jwtService.getIdFromToken(token);

                String blackListToken = redisBlackListRepository.get(ACCESS_TOKEN_PREFIX + userId).orElse("");

                // black list token
                if (token.equals(blackListToken)) {
                    throw new RuntimeException("logout user");
                }

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Not Found User"));

                Authentication authentication = new UsernamePasswordAuthenticationToken(user.getId(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException e) {
                throw e;
            } catch (JwtException e) {
                throw e;
            } catch (RuntimeException e) {
                throw e;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean matchPermitURL(HttpServletRequest request) {
        for (AntPathRequestMatcher requestMatcher : requestMatcherSet) {
            if (requestMatcher.matches(request)) {
                return true;
            }
        }

        return false;
    }

    private String resolveBearerAuthorizeHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        String token = "";

        if (authorizationHeader.startsWith("Bearer")) {
            token = authorizationHeader.substring(7);
        }

        return token;
    }
}
