package com.attendance.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtConfigValidator {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${spring.profiles.active:local}")
    private String activeProfile;
    
    @EventListener(ApplicationReadyEvent.class)
    @Order(0) // Run first, before other startup tasks
    public void validateJwtConfiguration() {
        log.info("Validating JWT configuration...");
        
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            log.error("❌ JWT_SECRET is not configured!");
            throw new IllegalStateException("JWT_SECRET environment variable must be set");
        }
        
        if (jwtSecret.length() < 32) {
            log.error("❌ JWT_SECRET must be at least 32 characters long. Current length: {}", jwtSecret.length());
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters long");
        }
        
        // Warn if using default secret in non-local environments
        String defaultSecret = "your-secret-key-must-be-at-least-32-characters-long-for-security";
        if (jwtSecret.equals(defaultSecret) && !"local".equals(activeProfile)) {
            log.error("❌ CRITICAL: Using default JWT secret in {} environment! This is insecure.", activeProfile);
            log.error("Please set JWT_SECRET environment variable to a secure random string (at least 32 characters)");
            throw new IllegalStateException("Cannot use default JWT secret in " + activeProfile + " environment. Set JWT_SECRET environment variable.");
        }
        
        if (jwtSecret.equals(defaultSecret)) {
            log.warn("⚠️  Using default JWT secret for local development. This is fine for local, but ensure JWT_SECRET is set in deployed environments.");
        } else {
            log.info("✅ JWT secret is configured (length: {})", jwtSecret.length());
        }
        
        log.info("JWT configuration validation completed");
    }
}
