package com.authcore.application.usecase;

import com.authcore.application.dto.AuthResponse;
import com.authcore.application.dto.LoginRequest;
import com.authcore.application.dto.RegisterRequest;
import com.authcore.application.dto.TokenRefreshRequest;
import com.authcore.domain.model.RefreshToken;
import com.authcore.domain.model.Role;
import com.authcore.domain.model.User;
import com.authcore.domain.repository.RefreshTokenRepository;
import com.authcore.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthUseCaseImpl implements AuthUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProvider tokenProvider;
    private final long refreshTokenDurationMs = 604800000L; // 7 days

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Error: Email is already in use!");
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(Set.of(Role.ROLE_USER))
                .build();

        userRepository.save(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new AuthException("Error: Bad credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Error: Bad credentials");
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        
        refreshTokenRepository.deleteByUserId(user.getId()); // Invalidate previous tokens
        
        String refreshTokenString = tokenProvider.generateRefreshToken(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .userId(user.getId())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();
        
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .username(user.getUsername())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new AuthException("Refresh token is not in database!"));
                
        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            refreshTokenRepository.deleteByToken(refreshToken.getToken());
            throw new AuthException("Refresh token was expired or revoked. Please make a new signin request");
        }
        
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new AuthException("User not found"));
                
        String accessToken = tokenProvider.generateAccessToken(user);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(requestRefreshToken)
                .username(user.getUsername())
                .build();
    }

    @Override
    @Transactional
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException("User not found"));
        refreshTokenRepository.deleteByUserId(user.getId());
    }
}
