package com.authcore.infrastructure.persistence.redis;

import com.authcore.domain.model.RefreshToken;
import com.authcore.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final SpringDataRedisTokenRepository repository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findById(token).map(this::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenRedisEntity entity = RefreshTokenRedisEntity.builder()
                .token(refreshToken.getToken())
                .userId(refreshToken.getUserId())
                .expiryDate(refreshToken.getExpiryDate())
                .revoked(refreshToken.isRevoked())
                .build();
        return toDomain(repository.save(entity));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(userId);
    }

    @Override
    public void deleteByToken(String token) {
        repository.deleteById(token);
    }

    private RefreshToken toDomain(RefreshTokenRedisEntity entity) {
        return RefreshToken.builder()
                .token(entity.getToken())
                .userId(entity.getUserId())
                .expiryDate(entity.getExpiryDate())
                .revoked(entity.isRevoked())
                .build();
    }
}
