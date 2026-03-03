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
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        
        if (refreshToken.getIsRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }
        
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired");
        }
        
        User user = refreshToken.getUser();
        
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
    }
    
    @Transactional
    public void logout(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        if (token.isPresent()) {
            token.get().setIsRevoked(true);
            refreshTokenRepository.save(token.get());
        }
    }
}
