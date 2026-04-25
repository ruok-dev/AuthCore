package com.authcore.infrastructure.config;

import lombok.extern.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class KeyConfig {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(KeyConfig.class);

    @Value("${JWT_PRIVATE_KEY:}")
    private String privateKeyStr;

    @Value("${JWT_PUBLIC_KEY:}")
    private String publicKeyStr;

    @Bean
    public RsaKeyProperties rsaKeyProperties() {
        RsaKeyProperties properties = new RsaKeyProperties();
        
        if (privateKeyStr != null && !privateKeyStr.isBlank() && publicKeyStr != null && !publicKeyStr.isBlank()) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                
                String privKeyPEM = privateKeyStr.replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
                byte[] decodedPrivKey = Base64.getDecoder().decode(privKeyPEM);
                properties.setPrivateKey((RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedPrivKey)));

                String pubKeyPEM = publicKeyStr.replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
                byte[] decodedPubKey = Base64.getDecoder().decode(pubKeyPEM);
                properties.setPublicKey((RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decodedPubKey)));
                
                log.info("RSA Keys loaded from environment variables successfully.");
                return properties;
            } catch (Exception e) {
                log.error("Failed to parse provided RSA keys. Falling back to generated ephemeral keys.", e);
            }
        }

        log.warn("No RSA Keys provided via environment variables. Generating ephemeral RSA keys for this session.");
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            properties.setPrivateKey((RSAPrivateKey) keyPair.getPrivate());
            properties.setPublicKey((RSAPublicKey) keyPair.getPublic());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate RSA keys", e);
        }
        
        return properties;
    }
}
