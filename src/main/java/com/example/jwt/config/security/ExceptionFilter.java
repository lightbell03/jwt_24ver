package com.example.jwt.config.security;

import com.example.jwt.dto.response.CommonResponse;
import com.example.jwt.error.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class ExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    public ExceptionFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, final FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.info("error []", e);
            errorResponse(response, ErrorType.EXPIRED_TOKEN);
        } catch (JwtException e) {
            log.info("error []", e);
            errorResponse(response, ErrorType.UNAUTHORIZATION);
        } catch (Exception e) {
            log.info("error []", e);
            errorResponse(response, ErrorType.INTERNAL_SERVER_ERROR);
        }
    }

    private void errorResponse(HttpServletResponse response, ErrorType errorType) throws IOException {
        response.setContentType("application/json");
        response.setStatus(errorType.getStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(new CommonResponse<Void>(errorType.getErrorCode(), null, errorType.getMessage())));
    }
}
