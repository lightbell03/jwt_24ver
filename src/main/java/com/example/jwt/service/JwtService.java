package com.example.jwt.service;

import com.example.jwt.config.property.JwtTokenProperties;
import com.example.jwt.dto.JwtUser;
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
        JwtUser user = (JwtUser) authentication.getPrincipal();
        Date now = new Date();
        return Jwts.builder()
                .id(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiration(new Date(expireTime + now.getTime()))
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
        Claims claims = Jwts.parser()
                .verifyWith(SECURITY_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getId());
    }

    public Date getDateFromToken(String token) {
        return Jwts.parser().verifyWith(SECURITY_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(SECURITY_KEY)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

}
