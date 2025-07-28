package com.hiveform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "google.oauth")
public class GoogleOAuthConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String tokenUrl = "https://oauth2.googleapis.com/token";
    private String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
    private String scope = "email profile";
} 