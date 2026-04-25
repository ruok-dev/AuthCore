package com.authcore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private String token;
    private UUID userId;
    private Instant expiryDate;
    private boolean revoked;

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }

    public void revoke() {
        this.revoked = true;
    }
}
