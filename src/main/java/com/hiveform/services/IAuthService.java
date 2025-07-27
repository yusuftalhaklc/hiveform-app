package com.hiveform.services;

import com.hiveform.dto.auth.RegisterRequestDto;

import com.hiveform.dto.auth.VerifyEmailRequestDto;

import com.hiveform.dto.auth.LoginRequestDto;
import com.hiveform.dto.auth.AuthResponseDto;

public interface IAuthService {
    String register(RegisterRequestDto registerRequestDto);
    String verifyEmail(VerifyEmailRequestDto verifyEmailRequestDto);
    AuthResponseDto login(LoginRequestDto loginRequestDto);
}
