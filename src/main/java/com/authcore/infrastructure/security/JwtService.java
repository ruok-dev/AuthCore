package com.authcore.infrastructure.security;

import com.authcore.application.usecase.TokenProvider;
import com.authcore.domain.model.User;
import com.authcore.infrastructure.config.RsaKeyProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService implements TokenProvider {

    private final RsaKeyProperties rsaKeys;

    @Value("${jwt.access-token-expiration:900000}")
    private long jwtExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}")
    private long refreshExpiration;

    @Override
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toList()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(rsaKeys.getPrivateKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(rsaKeys.getPrivateKey())
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(rsaKeys.getPublicKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(rsaKeys.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
