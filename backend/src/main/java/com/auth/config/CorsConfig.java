package com.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CORS configuration to allow requests from React frontend.
 */
@Configuration
public class CorsConfig {

    private static final long DEFAULT_MAX_AGE_SECONDS = 3600L;

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String allowedOrigins;

    /** Registers allowed origins, methods, and headers for browser-to-backend requests. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(parseAllowedOrigins(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(DEFAULT_MAX_AGE_SECONDS);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> parseAllowedOrigins(String originList) {
        return Arrays.stream(originList.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .collect(Collectors.toList());
    }
}
