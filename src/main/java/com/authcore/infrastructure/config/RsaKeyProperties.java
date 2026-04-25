package com.authcore.infrastructure.config;

import lombok.Getter;
import lombok.Setter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Getter
@Setter
public class RsaKeyProperties {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
}
