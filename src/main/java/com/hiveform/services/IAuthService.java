package com.hiveform.services;

import com.hiveform.dto.auth.DtoRegisterIU;

import com.hiveform.dto.auth.DtoVerifyEmailIU;

import com.hiveform.dto.auth.DtoLoginIU;
import com.hiveform.dto.auth.DtoAuthResponse;

public interface IAuthService {
    String register(DtoRegisterIU registerRequestDto);
    String verifyEmail(DtoVerifyEmailIU verifyEmailRequestDto);
    DtoAuthResponse login(DtoLoginIU loginRequestDto);
}
