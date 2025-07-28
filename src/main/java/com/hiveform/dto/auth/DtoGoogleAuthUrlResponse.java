package com.hiveform.dto.auth;

import lombok.Data;

@Data
public class DtoGoogleAuthUrlResponse {
    private String authorizationUrl;
    private String state;
} 