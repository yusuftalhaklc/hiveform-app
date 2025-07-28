package com.hiveform.controller;

import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.auth.*;

public interface IAuthController {
    ResponseEntity<ApiResponse<DtoAuthResponse>> login(DtoLoginIU loginRequestDto, HttpServletRequest request);
    ResponseEntity<ApiResponse<String>> register(DtoRegisterIU registerRequestDto, HttpServletRequest request);
    ResponseEntity<ApiResponse<String>> verifyEmail(DtoVerifyEmailIU dto, HttpServletRequest request);
    ResponseEntity<ApiResponse<Void>> google(DtoGoogleAuthIU googleAuthRequestDto, HttpServletRequest request);
    ResponseEntity<ApiResponse<Void>> callback(String code, String state, HttpServletRequest request);
    ResponseEntity<ApiResponse<Void>> forgotPassword(DtoForgotPasswordIU forgotPasswordRequestDto, HttpServletRequest request);
    ResponseEntity<ApiResponse<Void>> resetPassword(DtoResetPasswordIU resetPasswordRequestDto, HttpServletRequest request);
}