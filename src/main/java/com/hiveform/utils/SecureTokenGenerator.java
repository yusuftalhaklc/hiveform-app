package com.hiveform.utils;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class SecureTokenGenerator {
    
    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    public String generateSecureToken(int length) {
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            token.append(ALPHANUMERIC_CHARS.charAt(secureRandom.nextInt(ALPHANUMERIC_CHARS.length())));
        }
        return token.toString();
    }
} 