package com.example.jwt.service;

import com.example.jwt.config.property.JwtTokenProperties;
import com.example.jwt.dto.JwtUser;
import com.example.jwt.dto.response.TokenResponse;
import com.example.jwt.entity.User;
import com.example.jwt.repository.RedisAuthRepository;
import com.example.jwt.repository.RedisBlackListRepository;
import com.example.jwt.repository.RedisTokenRepository;
import com.example.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional
public class AuthService implements UserDetailsService {
    private static final String ACCESS_TOKEN_PREFIX = "ACCESS_TOKEN:";
    private static final String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN:";
    private final Long ACCESS_TOKEN_EXPIRE_TIME;
    private final Long REFRESH_TOKEN_EXPIRE_TIME;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RedisTokenRepository redisTokenRepository;
    private final RedisBlackListRepository redisBlackListRepository;

    public AuthService(UserRepository userRepository, RedisTokenRepository redisTokenRepository, RedisBlackListRepository redisBlackListRepository, JwtService jwtService, JwtTokenProperties jwtTokenProperties) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.redisTokenRepository = redisTokenRepository;
        this.redisBlackListRepository = redisBlackListRepository;
        this.ACCESS_TOKEN_EXPIRE_TIME = jwtTokenProperties.getAccessTokenExpireTime();
        this.REFRESH_TOKEN_EXPIRE_TIME = jwtTokenProperties.getRefreshTokenExpireTime();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("user id not found"));

        return JwtUser.of(user.getId(), user.getUserId(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public void logout(Authentication authentication) {
        JwtUser user = (JwtUser) authentication.getPrincipal();

        String accessToken = redisTokenRepository.get(ACCESS_TOKEN_PREFIX + user.getId())
                .orElseThrow(() -> new RuntimeException("no access token"));
        String refreshToken = redisTokenRepository.get(REFRESH_TOKEN_PREFIX + user.getId())
                .orElseThrow(() -> new RuntimeException("no refresh token"));

        saveTokenRedis(redisBlackListRepository, user.getId(), accessToken, refreshToken);
    }

    public TokenResponse refreshToken(String refreshToken) {
        Long userId = jwtService.getIdFromToken(refreshToken);

        String bannedRefreshToken = redisBlackListRepository.get(REFRESH_TOKEN_PREFIX + userId)
                .orElse("");

        if (refreshToken.equals(bannedRefreshToken)) {
            throw new RuntimeException("invalid user");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Not Found User"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getId(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        TokenResponse tokenResponse = generateToken(authentication);
        saveTokenRedis(redisTokenRepository, user.getId(), tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());

        return tokenResponse;
    }

    public TokenResponse generateToken(Authentication authentication) {
        String accessToken = jwtService.createAccessToken(authentication);
        String refreshToken = jwtService.createRefreshToken(authentication);

        JwtUser user = (JwtUser) authentication.getPrincipal();

        saveTokenRedis(redisTokenRepository, user.getId(), accessToken, refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    private void saveTokenRedis(RedisAuthRepository redisAuthRepository, Long userId, String accessToken, String refreshToken) {
        redisAuthRepository.save(ACCESS_TOKEN_PREFIX + userId, accessToken, ACCESS_TOKEN_EXPIRE_TIME);
        redisAuthRepository.save(REFRESH_TOKEN_PREFIX + userId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME);
    }
}
