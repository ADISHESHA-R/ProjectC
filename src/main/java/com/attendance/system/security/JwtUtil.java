package com.attendance.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {
    
    @Value("${app.jwt.secret}")
    private String secret;
    
    @Value("${app.jwt.expiration:1800000}")  // 30 minutes default
    private Long accessTokenExpiration;
    
    private SecretKey getSigningKey() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("JWT secret is not configured. Please set JWT_SECRET environment variable.");
        }
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long. Current length: " + secret.length());
        }
        // Warn if using default secret in production
        if (secret.equals("your-secret-key-must-be-at-least-32-characters-long-for-security")) {
            log.warn("⚠️ WARNING: Using default JWT secret! This is insecure for production. Please set JWT_SECRET environment variable.");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateAccessToken(String email, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("type", "access");
        return createToken(claims, email, accessTokenExpiration);
    }
    
    public String generateRefreshToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return createToken(claims, email, 7L * 24 * 60 * 60 * 1000);
    }
    
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> {
            Object userId = claims.get("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            }
            return (Long) userId;
        });
    }
    
    public String extractRole(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object roleObj = claims.get("role");
            if (roleObj != null) {
                return roleObj.toString();
            }
            log.warn("Role claim not found in JWT token");
            return null;
        } catch (Exception e) {
            log.error("Error extracting role from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Returns the token type claim: "access" or "refresh". Returns null if missing or on error.
     */
    public String extractType(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object typeObj = claims.get("type");
            return typeObj != null ? typeObj.toString() : null;
        } catch (Exception e) {
            log.error("Error extracting type from token: {}", e.getMessage());
            return null;
        }
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token format: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.error("JWT signature does not match. This usually means the token was signed with a different secret. Check JWT_SECRET environment variable matches the environment where the token was generated.");
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error parsing JWT token: {}", e.getMessage());
            throw e;
        }
    }
    
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    public Boolean validateToken(String token) {
        try {
            String email = extractEmail(token);
            return email != null && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    public Boolean validateToken(String token, String email) {
        try {
            final String tokenEmail = extractEmail(token);
            return (tokenEmail != null && tokenEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}
