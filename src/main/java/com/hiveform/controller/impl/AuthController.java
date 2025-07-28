
package com.hiveform.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.hiveform.services.IAuthService;

import jakarta.validation.Valid;

import com.hiveform.dto.auth.DtoForgotPasswordIU;
import com.hiveform.dto.auth.DtoGoogleAuthIU;
import com.hiveform.dto.auth.DtoLoginIU;
import com.hiveform.dto.auth.DtoRegisterIU;
import com.hiveform.dto.auth.DtoResetPasswordIU;
import com.hiveform.dto.auth.DtoVerifyEmailIU;
import com.hiveform.dto.auth.DtoAuthResponse;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.RootResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<DtoAuthResponse>> login(@Valid @RequestBody DtoLoginIU loginRequestDto, HttpServletRequest request) {
        DtoAuthResponse response = authService.login(loginRequestDto);
        return ResponseEntity.ok(RootResponse.success(response, "Login successful", request.getRequestURI()));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody DtoRegisterIU registerRequestDto, HttpServletRequest request) {
        String message = authService.register(registerRequestDto);
        return ResponseEntity.ok(RootResponse.success(message, "Registration successful", request.getRequestURI()));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@Valid @RequestBody DtoVerifyEmailIU dto, HttpServletRequest request) {
        String result = authService.verifyEmail(dto);
        return ResponseEntity.ok(RootResponse.success(result, "Email verification result", request.getRequestURI()));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<Void>> google(@Valid @RequestBody DtoGoogleAuthIU googleAuthRequestDto, HttpServletRequest request) {
        // TODO: implement google auth logic
        return ResponseEntity.ok(RootResponse.success(null, "Google auth not implemented", request.getRequestURI()));
    }

    @GetMapping("/google/callback")
    public ResponseEntity<ApiResponse<Void>> callback(@RequestParam(required = true) String code, @RequestParam(required = true) String state, HttpServletRequest request) {
        // TODO: implement callback logic
        return ResponseEntity.ok(RootResponse.success(null, "Google callback not implemented", request.getRequestURI()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody DtoForgotPasswordIU forgotPasswordRequestDto, HttpServletRequest request) {
        // TODO: implement forgot password logic
        return ResponseEntity.ok(RootResponse.success(null, "Forgot password not implemented", request.getRequestURI()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody DtoResetPasswordIU resetPasswordRequestDto, HttpServletRequest request) {
        // TODO: implement reset password logic
        return ResponseEntity.ok(RootResponse.success(null, "Reset password not implemented", request.getRequestURI()));
    }

}
