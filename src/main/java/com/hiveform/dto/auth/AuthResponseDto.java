package com.hiveform.dto.auth;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private Long expireAt;
}
