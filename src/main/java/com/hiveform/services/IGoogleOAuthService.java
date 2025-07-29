package com.hiveform.services;

import com.hiveform.dto.auth.GoogleAuthRequest;
import com.hiveform.dto.auth.AuthResponse;
import com.hiveform.dto.auth.GoogleAuthUrlResponse;

public interface IGoogleOAuthService {
    AuthResponse authenticateWithGoogle(GoogleAuthRequest googleAuthRequestDto);
    GoogleAuthUrlResponse generateAuthorizationUrl();
    AuthResponse handleOAuthCallback(String code, String state);
} 