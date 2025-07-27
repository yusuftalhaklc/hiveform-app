package com.hiveform.controller;

import org.springframework.http.ResponseEntity;

import com.hiveform.dto.auth.ForgotPasswordRequestDto;
import com.hiveform.dto.auth.GoogleAuthRequestDto;
import com.hiveform.dto.auth.LoginRequestDto;
import com.hiveform.dto.auth.RegisterRequestDto;
import com.hiveform.dto.auth.ResetPasswordRequestDto;

public interface IAuthController {
    ResponseEntity<?> login(LoginRequestDto loginRequestDto);
    ResponseEntity<?> register(RegisterRequestDto registerRequestDto);
    ResponseEntity<?> google(GoogleAuthRequestDto googleAuthRequestDto);
    ResponseEntity<?> callback(String code, String state);
    ResponseEntity<?> forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto);
    ResponseEntity<?> resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);
}