package com.attendance.system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Skip JWT validation for public endpoints
        if (path.startsWith("/api/auth/") || 
            path.startsWith("/swagger-ui") || 
            path.startsWith("/v3/api-docs") || 
            path.startsWith("/swagger-resources") ||
            path.startsWith("/webjars") ||
            path.startsWith("/actuator/health") ||
            path.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt)) {
                log.info("JWT token found for path: {}", path);
                try {
                    if (jwtUtil.validateToken(jwt)) {
                        String email = jwtUtil.extractEmail(jwt);
                        String role = jwtUtil.extractRole(jwt);
                        Long userId = jwtUtil.extractUserId(jwt);
                        
                        log.info("JWT Authentication - Email: {}, Role: {}, UserId: {}", email, role, userId);
                        
                        if (email != null && userId != null) {
                            // Clear any existing authentication
                            SecurityContextHolder.clearContext();
                            
                            // Ensure role is not null
                            if (role == null || role.isEmpty()) {
                                log.warn("Role is null or empty in JWT token for user: {}", email);
                                role = "EMPLOYEE"; // Default role if missing
                            }
                            
                            String authority = "ROLE_" + role.toUpperCase();
                            log.info("Setting authentication with authority: {} for user: {}", authority, email);
                            
                            UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority(authority))
                                );
                            
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            
                            log.info("✅ Authentication set successfully for user: {} (ID: {}) with role: {}", email, userId, role);
                        } else {
                            log.warn("Email or UserId is null in JWT token - Email: {}, UserId: {}", email, userId);
                            SecurityContextHolder.clearContext();
                        }
                    } else {
                        log.warn("❌ JWT token validation failed for path: {}. Token may be expired, invalid, or signed with a different secret.", path);
                        SecurityContextHolder.clearContext();
                    }
                } catch (io.jsonwebtoken.ExpiredJwtException e) {
                    log.warn("❌ JWT token expired for path: {}. Expired at: {}. Please login again or use refresh token endpoint.", path, e.getClaims().getExpiration());
                    SecurityContextHolder.clearContext();
                } catch (io.jsonwebtoken.security.SignatureException e) {
                    log.error("❌ JWT signature mismatch for path: {}. Token was signed with a different secret. Error: {}", path, e.getMessage());
                    SecurityContextHolder.clearContext();
                } catch (Exception e) {
                    log.error("❌ JWT token validation error for path {}: {}", path, e.getMessage(), e);
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.info("No JWT token found in request for path: {}. Authorization header: {}", path, request.getHeader("Authorization") != null ? "present but invalid format" : "missing");
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            log.error("❌ Cannot set user authentication for path {}: {}", path, e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
