package com.website.api.mosi;

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
                registry.addMapping("/api/time")
                        .allowedOrigins("http://localhost:8000", "http://localhost:63342", "http://127.0.0.1:8000","https://fmohsen22.github.io")
                        .allowedMethods("GET");
            }
        };
    }
}
