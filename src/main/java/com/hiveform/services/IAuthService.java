package com.hiveform.services;

import com.hiveform.dto.auth.RegisterRequest;
import com.hiveform.dto.auth.VerifyEmailRequest;
import com.hiveform.dto.auth.LoginRequest;
import com.hiveform.dto.auth.AuthResponse;
import com.hiveform.dto.auth.ForgotPasswordRequest;
import com.hiveform.dto.auth.ResetPasswordRequest;

public interface IAuthService {
    void register(RegisterRequest registerRequestDto);
    void verifyEmail(VerifyEmailRequest verifyEmailRequestDto);
    AuthResponse login(LoginRequest loginRequestDto);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequestDto);
    void resetPassword(ResetPasswordRequest resetPasswordRequestDto);
}
