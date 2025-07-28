package com.hiveform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer:hiveform}")
    private String issuer;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateTokenWithClaims(JwtClaim jwtClaim) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", jwtClaim.getUserId());
        claims.put("email", jwtClaim.getEmail());
        claims.put("fullname", jwtClaim.getFullname());
        claims.put("role", jwtClaim.getRole());
        
        return generateToken(claims, jwtClaim.getEmail(), jwtExpiration);
    }

    public String generateToken(Map<String, Object> claims, String subject, long expirationMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims()
                    .empty()
                    .add(claims)
                    .subject(subject)
                    .issuer(issuer)
                    .issuedAt(new Date(now))
                    .expiration(new Date(now + expirationMillis))
                    .and()
                .signWith(getSigningKey())
                .compact();
    }

    public JwtClaim createJwtClaim(String userId, String email, String fullname, String role) {
        long now = System.currentTimeMillis() / 1000;
        long exp = (now + jwtExpiration / 1000);
        
        return JwtClaim.builder()
                .userId(userId)
                .email(email)
                .fullname(fullname)
                .role(role)
                .iss(issuer)
                .iat(now)
                .exp(exp)
                .build();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public JwtClaim extractJwtClaim(String token) {
        Claims claims = extractAllClaims(token);
        return JwtClaim.builder()
                .userId(claims.get("userId", String.class))
                .email(claims.get("email", String.class))
                .fullname(claims.get("fullname", String.class))
                .role(claims.get("role", String.class))
                .exp(claims.getExpiration().getTime())
                .iss(claims.getIssuer())
                .iat(claims.getIssuedAt().getTime())
                .build();
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email)) && !isTokenExpired(token);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }
}