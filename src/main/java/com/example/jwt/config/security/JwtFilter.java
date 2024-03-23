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
            Set.of(new AntPathRequestMatcher("/auth/login"),
                    new AntPathRequestMatcher("/auth/token/refresh"),
                    new AntPathRequestMatcher("/users/sign-up"),
                    new AntPathRequestMatcher("/ws/**"));
    private static final String ACCESS_TOKEN_PREFIX = "ACCESS_TOKEN:";
    private final JwtService jwtService;
    private final RedisAuthRepository redisTokenRepository;
    private final RedisAuthRepository redisBlackListRepository;
    private final UserRepository userRepository;

    public JwtFilter(JwtService jwtService, RedisAuthRepository redisTokenRepository, RedisAuthRepository redisBlackListRepository, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.redisTokenRepository = redisTokenRepository;
        this.redisBlackListRepository = redisBlackListRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!matchPermitURL(request)) {
            log.info("method = {}", request.getMethod());
            log.info("url = {}", request.getRequestURL());
            log.info("protocol = {}", request.getProtocol());

            String token = resolveBearerAuthorizeHeader(request);
            log.info("token = {}", token);
            try {
                jwtService.validateToken(token);
            } catch (Exception e) {
                throw e;
            }

            Long userId = jwtService.getIdFromToken(token);

            if (!isValidToken(userId, token)) {
                throw new RuntimeException("unAuthorization user");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Not Found User"));

            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getId(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        filterChain.doFilter(request, response);

        log.info("status = {}", response.getStatus());
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

    private boolean isValidToken(Long id, String token) {
        String storedAccessToken = redisTokenRepository.get(ACCESS_TOKEN_PREFIX + id).orElse("");

        // 토큰 저장이 되어 있으면
        if (storedAccessToken.equals(token)) {
            return true;
        }

        String blackListAccessToken = redisBlackListRepository.get(ACCESS_TOKEN_PREFIX + id).orElse("");
        return !blackListAccessToken.equals(token);
    }
}
