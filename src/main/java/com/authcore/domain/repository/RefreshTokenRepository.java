package com.authcore.domain.repository;

import com.authcore.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken save(RefreshToken refreshToken);
    void deleteByUserId(UUID userId);
    void deleteByToken(String token);
}
