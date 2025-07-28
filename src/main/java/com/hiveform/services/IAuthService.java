package com.hiveform.services;

import com.hiveform.dto.auth.DtoRegisterIU;

import com.hiveform.dto.auth.DtoVerifyEmailIU;

import com.hiveform.dto.auth.DtoLoginIU;
import com.hiveform.dto.auth.DtoAuthResponse;
import com.hiveform.dto.auth.DtoForgotPasswordIU;
import com.hiveform.dto.auth.DtoResetPasswordIU;

public interface IAuthService {
    void register(DtoRegisterIU registerRequestDto);
    void verifyEmail(DtoVerifyEmailIU verifyEmailRequestDto);
    DtoAuthResponse login(DtoLoginIU loginRequestDto);
    void forgotPassword(DtoForgotPasswordIU forgotPasswordRequestDto);
    void resetPassword(DtoResetPasswordIU resetPasswordRequestDto);
}
