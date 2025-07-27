package com.hiveform.dto.auth;

import lombok.Data;

@Data
public class VerifyEmailRequestDto {
    private String email;
    private String code;
}
