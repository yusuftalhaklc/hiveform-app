package com.hiveform.controller;

import org.springframework.http.ResponseEntity;

import com.hiveform.dto.auth.DtoForgotPasswordIU;
import com.hiveform.dto.auth.DtoGoogleAuthIU;
import com.hiveform.dto.auth.DtoLoginIU;
import com.hiveform.dto.auth.DtoRegisterIU;
import com.hiveform.dto.auth.DtoResetPasswordIU;

public interface IAuthController {
    ResponseEntity<?> login(DtoLoginIU loginRequestDto);
    ResponseEntity<?> register(DtoRegisterIU registerRequestDto);
    ResponseEntity<?> google(DtoGoogleAuthIU googleAuthRequestDto);
    ResponseEntity<?> callback(String code, String state);
    ResponseEntity<?> forgotPassword(DtoForgotPasswordIU forgotPasswordRequestDto);
    ResponseEntity<?> resetPassword(DtoResetPasswordIU resetPasswordRequestDto);
}