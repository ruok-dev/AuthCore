package com.authcore.application.usecase;

import com.authcore.domain.model.User;

public interface TokenProvider {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
}
