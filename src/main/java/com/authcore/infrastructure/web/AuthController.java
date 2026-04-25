package com.authcore.infrastructure.web;

import com.authcore.application.dto.AuthResponse;
import com.authcore.application.dto.LoginRequest;
import com.authcore.application.dto.RegisterRequest;
import com.authcore.application.dto.TokenRefreshRequest;
import com.authcore.application.usecase.AuthException;
import com.authcore.application.usecase.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and authorization")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        authUseCase.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get tokens")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authUseCase.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        AuthResponse response = authUseCase.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and invalidate tokens")
    public ResponseEntity<?> logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            authUseCase.logout(authentication.getName());
            return ResponseEntity.ok(Map.of("message", "Log out successful!"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Not authenticated"));
    }
    
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuthException(AuthException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}
