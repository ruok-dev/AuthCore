package com.authcore.application.usecase;

import com.authcore.application.dto.AuthResponse;
import com.authcore.application.dto.LoginRequest;
import com.authcore.application.dto.RegisterRequest;
import com.authcore.application.dto.TokenRefreshRequest;

public interface AuthUseCase {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(TokenRefreshRequest request);
    void logout(String username);
}
