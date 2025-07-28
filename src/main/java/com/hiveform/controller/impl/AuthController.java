
package com.hiveform.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.hiveform.services.IAuthService;
import com.hiveform.services.IGoogleOAuthService;

import jakarta.validation.Valid;

import com.hiveform.dto.auth.DtoForgotPasswordIU;
import com.hiveform.dto.auth.DtoGoogleAuthUrlResponse;
import com.hiveform.dto.auth.DtoLoginIU;
import com.hiveform.dto.auth.DtoRegisterIU;
import com.hiveform.dto.auth.DtoResetPasswordIU;
import com.hiveform.dto.auth.DtoVerifyEmailIU;
import com.hiveform.dto.auth.DtoAuthResponse;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.RootResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @Autowired
    private IGoogleOAuthService googleOAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<DtoAuthResponse>> login(@Valid @RequestBody DtoLoginIU loginRequestDto, HttpServletRequest request) {
        DtoAuthResponse response = authService.login(loginRequestDto);
        return ResponseEntity.ok(RootResponse.success(response, "Login successful", request.getRequestURI()));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody DtoRegisterIU registerRequestDto, HttpServletRequest request) {
        authService.register(registerRequestDto);
        return ResponseEntity.ok(RootResponse.success(null, "Registration successful and email verification sent", request.getRequestURI()));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody DtoVerifyEmailIU dto, HttpServletRequest request) {
        authService.verifyEmail(dto);
        return ResponseEntity.ok(RootResponse.success(null, "Email verification successful", request.getRequestURI()));
    }

    @GetMapping("/google")
    public void googleAuthorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DtoGoogleAuthUrlResponse authorizationUrl = googleOAuthService.generateAuthorizationUrl();
        response.sendRedirect(authorizationUrl.getAuthorizationUrl());
    }

    @GetMapping("/google/authorize")
    public ResponseEntity<ApiResponse<DtoAuthResponse>> googleCallback(@RequestParam String code, @RequestParam String state, HttpServletRequest request) {
        DtoAuthResponse authResponse = googleOAuthService.handleOAuthCallback(code, state);
        return ResponseEntity.ok(RootResponse.success(authResponse, "Google authentication successful", request.getRequestURI()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody DtoForgotPasswordIU forgotPasswordRequestDto, HttpServletRequest request) {
        authService.forgotPassword(forgotPasswordRequestDto);
        return ResponseEntity.ok(RootResponse.success(null, "Password reset email sent if account exists", request.getRequestURI()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody DtoResetPasswordIU resetPasswordRequestDto, HttpServletRequest request) {
        authService.resetPassword(resetPasswordRequestDto);
        return ResponseEntity.ok(RootResponse.success(null, "Password reset successful", request.getRequestURI()));
    }

}
