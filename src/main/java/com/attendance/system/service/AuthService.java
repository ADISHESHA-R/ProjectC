package com.attendance.system.service;

import com.attendance.system.dto.request.LoginRequest;
import com.attendance.system.dto.request.RefreshTokenRequest;
import com.attendance.system.dto.response.AuthResponse;
import com.attendance.system.entity.RefreshToken;
import com.attendance.system.entity.User;
import com.attendance.system.enums.UserStatus;
import com.attendance.system.repository.RefreshTokenRepository;
import com.attendance.system.repository.UserRepository;
import com.attendance.system.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("User account is inactive");
        }
        
        refreshTokenRepository.revokeAllUserTokens(user);
        
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());
        
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenEntity.setIsRevoked(false);
        refreshTokenRepository.save(refreshTokenEntity);
        
        return new AuthResponse(
            accessToken,
            refreshToken,
            "Bearer",
            jwtUtil.extractExpiration(accessToken).getTime() - System.currentTimeMillis()
        );
    }
    
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String tokenString = request.getRefreshToken();
        
        // First validate the JWT token structure and signature
        try {
            if (!jwtUtil.validateToken(tokenString)) {
                throw new RuntimeException("Refresh token is invalid or expired. Please login again.");
            }
            
            // Verify it's actually a refresh token (has type claim)
            try {
                String email = jwtUtil.extractEmail(tokenString);
                Long userId = jwtUtil.extractUserId(tokenString);
                
                // Try to find the token in database
                Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(tokenString);
                if (tokenOptional.isEmpty()) {
                    // Token is valid JWT but not in database - likely from different environment
                    throw new RuntimeException("Refresh token not found. This token may have been generated in a different environment. Please login again to get a new token.");
                }
                RefreshToken refreshToken = tokenOptional.get();
                
                if (refreshToken.getIsRevoked()) {
                    throw new RuntimeException("Refresh token has been revoked. Please login again.");
                }
                
                if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                    throw new RuntimeException("Refresh token has expired. Please login again.");
                }
                
                User user = refreshToken.getUser();
                
                // Verify user from token matches user from database
                if (!user.getId().equals(userId) || !user.getEmail().equals(email)) {
                    throw new RuntimeException("Refresh token user mismatch. Please login again.");
                }
                
                if (user.getStatus() != UserStatus.ACTIVE) {
                    throw new RuntimeException("User account is inactive");
                }
                
                String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId(), user.getRole().name());
                
                return new AuthResponse(
                    newAccessToken,
                    refreshToken.getToken(),
                    "Bearer",
                    jwtUtil.extractExpiration(newAccessToken).getTime() - System.currentTimeMillis()
                );
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                throw new RuntimeException("Refresh token has expired. Please login again.");
            } catch (io.jsonwebtoken.security.SignatureException e) {
                throw new RuntimeException("Refresh token signature is invalid. This token may have been generated with a different JWT secret. Please login again.");
            }
        } catch (RuntimeException e) {
            // Re-throw our custom exceptions
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token: " + e.getMessage() + ". Please login again.");
        }
    }
    
    @Transactional
    public void logout(String refreshToken) {
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(refreshToken);
        if (tokenOptional.isPresent()) {
            RefreshToken token = tokenOptional.get();
            token.setIsRevoked(true);
            refreshTokenRepository.save(token);
        }
    }
}
