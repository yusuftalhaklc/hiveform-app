package com.hiveform.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleAuthUrlResponse {
    private String authorizationUrl;
    private String state;
} 