package com.example.blackjack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import com.example.blackjack.security.JwtChannelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.config.ChannelRegistration;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtChannelInterceptor jwtChannelInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Add our interceptor to the chain
        registration.interceptors(jwtChannelInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The endpoint that clients will connect to.
        // SockJS is a fallback for browsers that don't support WebSocket.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("http://localhost:4200").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Defines message destinations starting with "/app".
        // These are messages bound for @MessageMapping-annotated methods in controllers.
        registry.setApplicationDestinationPrefixes("/app");

        // Defines the prefix for destinations that the broker will handle (e.g., broadcasting to clients).
        // Clients will subscribe to topics like "/topic/public" or "/topic/game/123".
        registry.enableSimpleBroker("/topic");
    }
}