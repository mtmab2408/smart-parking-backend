package com.smartpark.parking_backend.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final SlotWebSocketHandler slotWebSocketHandler;

    public WebSocketConfig(SlotWebSocketHandler slotWebSocketHandler) {
        this.slotWebSocketHandler = slotWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(slotWebSocketHandler, "/ws/slots")
            .setAllowedOrigins("*");
    }
}
