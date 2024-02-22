package com.example.jwt.service;

import com.example.jwt.config.property.JwtTokenProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey SECURITY_KEY;
    private final Long ACCESS_TOKEN_EXPIRE_TIME;
    private final Long REFRESH_TOKEN_EXPIRE_TIME;

    public JwtService(JwtTokenProperties jwtTokenProperties) {
        SECURITY_KEY = Keys.hmacShaKeyFor(jwtTokenProperties.getSecurityKey().getBytes());
        ACCESS_TOKEN_EXPIRE_TIME = jwtTokenProperties.getAccessTokenExpireTime();
        REFRESH_TOKEN_EXPIRE_TIME = jwtTokenProperties.getRefreshTokenExpireTime();
    }

    private String createToken(Authentication authentication, Long expireTime) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date();
        return Jwts.builder()
                .id(user.getUsername())
                .issuedAt(now)
                .expiration(new Date(expireTime))
                .signWith(SECURITY_KEY)
                .compact();
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public Long getIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECURITY_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.parseLong(claims.getId());
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException e) {
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(SECURITY_KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException e) {
            return false;
        }
    }

}
