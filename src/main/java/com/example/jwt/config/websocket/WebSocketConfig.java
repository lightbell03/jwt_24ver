package com.example.jwt.config.websocket;

import com.example.jwt.config.websocket.handler.ChatHandler;
import com.example.jwt.config.websocket.interceptor.WebSocketAuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;
    private final HandshakeHandler chatHandshakeHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/ws/chat")
                .addInterceptors(new WebSocketAuthorizationInterceptor())
                .setAllowedOrigins("**")
                ;
    }
}
