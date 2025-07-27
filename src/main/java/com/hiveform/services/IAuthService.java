package com.hiveform.services;

import com.hiveform.dto.auth.RegisterRequestDto;

import com.hiveform.dto.auth.VerifyEmailRequestDto;

public interface IAuthService {
    String register(RegisterRequestDto registerRequestDto);
    String verifyEmail(VerifyEmailRequestDto verifyEmailRequestDto);
}
