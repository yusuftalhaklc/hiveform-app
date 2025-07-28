package com.hiveform.services;

import com.hiveform.dto.auth.DtoGoogleAuth;
import com.hiveform.dto.auth.DtoAuthResponse;
import com.hiveform.dto.auth.DtoGoogleAuthUrlResponse;

public interface IGoogleOAuthService {
    DtoAuthResponse authenticateWithGoogle(DtoGoogleAuth googleAuthRequestDto);
    DtoGoogleAuthUrlResponse generateAuthorizationUrl();
    DtoAuthResponse handleOAuthCallback(String code, String state);
} 