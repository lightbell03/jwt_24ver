package com.example.jwt.service;

import com.example.jwt.config.property.JwtTokenProperties;
import com.example.jwt.dto.JwtUser;
import com.example.jwt.dto.response.TokenResponse;
import com.example.jwt.entity.User;
import com.example.jwt.repository.RedisAuthRepository;
import com.example.jwt.repository.RedisBlackListRepository;
import com.example.jwt.repository.RedisTokenRepository;
import com.example.jwt.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

@Service
@Slf4j
@Transactional
public class AuthService implements UserDetailsService {
//    private static final long sevenDays = 1000 * 60 * 60 * 24 * 7;
    private static final long sevenDays = 1000 * 60 * 4;
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

    public TokenResponse login(Authentication authentication) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        TokenResponse tokenResponse = generateToken(authentication);

        saveTokenRedis(redisTokenRepository, jwtUser.getId(), tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());

        return tokenResponse;
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
        try {
            Date now = new Date();
            Date afterSevenDayFromNow = new Date(now.getTime() + sevenDays);
            Date tokenRemainTime = jwtService.getDateFromToken(refreshToken);

            log.info("token remain date = {}", tokenRemainTime);

            Long userId = jwtService.getIdFromToken(refreshToken);

            String storedRefreshToken = redisTokenRepository.get(REFRESH_TOKEN_PREFIX + userId + ":" + refreshToken).orElse("");

            if(!storedRefreshToken.equals(refreshToken)) {
                throw new RuntimeException("not found refresh token");
            }

            // refresh token 이 블랙리스트에 존재하는 토큰인지 확인
//            String bannedRefreshToken = redisBlackListRepository.get(REFRESH_TOKEN_PREFIX + userId)
//                    .orElse("");
//            if (bannedRefreshToken.equals(refreshToken)) {
//                throw new RuntimeException("blacklist user");
//            }

            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Not Found User"));
            JwtUser jwtUser = JwtUser.of(user.getId(), user.getUserId(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

            Authentication authentication = new UsernamePasswordAuthenticationToken(jwtUser, null, jwtUser.getAuthorities());

            String accessToken = jwtService.createAccessToken(authentication);
            redisTokenRepository.save(ACCESS_TOKEN_PREFIX + userId, accessToken, ACCESS_TOKEN_EXPIRE_TIME);

            if(afterSevenDayFromNow.after(tokenRemainTime)) {
                log.info("create refresh token");
                // 이전 refresh token 유효 시간 짧게 변경
                redisTokenRepository.save(REFRESH_TOKEN_PREFIX + userId + ":" + refreshToken, refreshToken, 5000L);
                refreshToken = jwtService.createRefreshToken(authentication);
                redisTokenRepository.save(REFRESH_TOKEN_PREFIX + userId + ":" + refreshToken, refreshToken, REFRESH_TOKEN_EXPIRE_TIME);
            }

            return new TokenResponse(accessToken, refreshToken);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("invalid refresh token, " + e.getMessage());
        }
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
        redisAuthRepository.save(REFRESH_TOKEN_PREFIX + userId + ":" + refreshToken, refreshToken, REFRESH_TOKEN_EXPIRE_TIME);
    }
}
