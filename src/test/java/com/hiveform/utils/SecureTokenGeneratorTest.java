package com.hiveform.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SecureTokenGeneratorTest {

    private SecureTokenGenerator tokenGenerator;

    @BeforeEach
    void setUp() {
        tokenGenerator = new SecureTokenGenerator();
    }

    @Test
    void testGenerateSecureToken() {
        String token = tokenGenerator.generateSecureToken(10);
        assertEquals(10, token.length());
        assertTrue(token.matches("[A-Za-z0-9]+"));
    }

    @Test
    void testGenerateSecureTokenWithDifferentLengths() {
        String token8 = tokenGenerator.generateSecureToken(8);
        String token16 = tokenGenerator.generateSecureToken(16);
        String token64 = tokenGenerator.generateSecureToken(64);
        
        assertEquals(8, token8.length());
        assertEquals(16, token16.length());
        assertEquals(64, token64.length());
        
        assertTrue(token8.matches("[A-Za-z0-9]+"));
        assertTrue(token16.matches("[A-Za-z0-9]+"));
        assertTrue(token64.matches("[A-Za-z0-9]+"));
    }

    @Test
    void testTokenUniqueness() {
        String token1 = tokenGenerator.generateSecureToken(16);
        String token2 = tokenGenerator.generateSecureToken(16);
        assertNotEquals(token1, token2);
    }
} 