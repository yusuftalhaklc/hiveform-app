
package com.hiveform.entities;

import java.util.UUID;

import com.hiveform.enums.AuthProvider;
import com.hiveform.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="full_name", length = 100, nullable = false, unique = false)
    private String fullName;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "password", nullable = false) 
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "refresh_token", unique = true, length = 64)
    private String refreshToken;
    
    @Column(name = "refresh_token_expiry")
    private Long refreshTokenExpiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "email_verification_code", length = 40)
    private String emailVerificationCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    @PrePersist
    protected void onCreate() {
        long timestamp = System.currentTimeMillis() / 1000;
        createdAt = timestamp;
        updatedAt = timestamp;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis() / 1000;
    }
}