package com.authcore.infrastructure.persistence.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "RefreshToken", timeToLive = 604800) // 7 days in seconds
public class RefreshTokenRedisEntity {
    @Id
    private String token;
    
    @Indexed
    private UUID userId;
    
    private Instant expiryDate;
    private boolean revoked;
}
