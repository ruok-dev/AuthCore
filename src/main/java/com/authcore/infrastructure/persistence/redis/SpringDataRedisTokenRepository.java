package com.authcore.infrastructure.persistence.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataRedisTokenRepository extends CrudRepository<RefreshTokenRedisEntity, String> {
    List<RefreshTokenRedisEntity> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
