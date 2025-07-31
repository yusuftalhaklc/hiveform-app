
package com.hiveform.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.hiveform.services.IAuthService;
import com.hiveform.services.IGoogleOAuthService;

import jakarta.validation.Valid;

import com.hiveform.dto.auth.ForgotPasswordRequest;
import com.hiveform.dto.auth.GoogleAuthUrlResponse;
import com.hiveform.dto.auth.LoginRequest;
import com.hiveform.dto.auth.RegisterRequest;
import com.hiveform.dto.auth.ResetPasswordRequest;
import com.hiveform.dto.auth.VerifyEmailRequest;
import com.hiveform.dto.auth.AuthResponse;
import com.hiveform.controller.IAuthController;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.RootResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication API")
public class AuthController implements IAuthController {

    @Autowired
    private IAuthService authService;

    @Autowired
    private IGoogleOAuthService googleOAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequestDto, HttpServletRequest request) {
        AuthResponse response = authService.login(loginRequestDto);
        return ResponseEntity.ok(RootResponse.success(response, "Login successful", request.getRequestURI()));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequestDto, HttpServletRequest request) {
        authService.register(registerRequestDto);
        return ResponseEntity.ok(RootResponse.success(null, "Registration successful and email verification sent", request.getRequestURI()));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
        @RequestParam String email, 
        @RequestParam String code, 
        HttpServletRequest request) {
        VerifyEmailRequest verifyEmailRequest = VerifyEmailRequest.builder()
                .email(email)
                .code(code)
                .build();
        
        authService.verifyEmail(verifyEmailRequest);
        return ResponseEntity.ok(RootResponse.success(null, "Email verification successful", request.getRequestURI()));
    }

    @GetMapping("/google")
    public void googleAuthorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GoogleAuthUrlResponse authorizationUrl = googleOAuthService.generateAuthorizationUrl();
        response.sendRedirect(authorizationUrl.getAuthorizationUrl());
    }

    @GetMapping("/google/authorize")
    public ResponseEntity<ApiResponse<AuthResponse>> googleCallback(@RequestParam String code, @RequestParam String state, HttpServletRequest request) {
        AuthResponse authResponse = googleOAuthService.handleOAuthCallback(code, state);
        return ResponseEntity.ok(RootResponse.success(authResponse, "Google authentication successful", request.getRequestURI()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequestDto, HttpServletRequest request) {
        authService.forgotPassword(forgotPasswordRequestDto);
        return ResponseEntity.ok(RootResponse.success(null, "Password reset email sent if account exists", request.getRequestURI()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequestDto, HttpServletRequest request) {
        authService.resetPassword(resetPasswordRequestDto);
        return ResponseEntity.ok(RootResponse.success(null, "Password reset successful", request.getRequestURI()));
    }
}
