package com.hiveform.controller;

import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.auth.*;

public interface IAuthController {
    ResponseEntity<ApiResponse<AuthResponse>> login(LoginRequest loginRequestDto, HttpServletRequest request);
    ResponseEntity<ApiResponse<String>> register(RegisterRequest registerRequestDto, HttpServletRequest request);
    ResponseEntity<ApiResponse<String>> verifyEmail(VerifyEmailRequest dto, HttpServletRequest request);
    ResponseEntity<ApiResponse<Void>> callback(String code, String state, HttpServletRequest request);
    ResponseEntity<ApiResponse<Void>> forgotPassword(ForgotPasswordRequest forgotPasswordRequestDto, HttpServletRequest request);
    ResponseEntity<ApiResponse<Void>> resetPassword(ResetPasswordRequest resetPasswordRequestDto, HttpServletRequest request);
}