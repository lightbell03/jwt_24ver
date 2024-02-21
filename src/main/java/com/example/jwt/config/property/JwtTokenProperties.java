package com.example.jwt.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt.token")
public class JwtTokenProperties {
    private Long accessTokenExpireTime;
    private Long refreshTokenExpireTime;
    private String securityKey;
}

