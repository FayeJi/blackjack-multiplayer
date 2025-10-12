package com.example.blackjack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // This configuration allows all GET, POST, PUT, DELETE, etc. methods
                // from the Angular development server origin.
                registry.addMapping("/**") // Apply to all endpoints
                        .allowedOrigins("http://localhost:4200") // Trust this origin
                        .allowedMethods("*") // Allow all methods
                        .allowedHeaders("*") // Allow all headers
                        .allowCredentials(true); // Allow cookies/auth headers
            }
        };
    }
}