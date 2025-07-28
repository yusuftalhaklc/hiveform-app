package com.hiveform.dto.auth;

import lombok.Data;

@Data
public class DtoAuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long expireAt;
}
