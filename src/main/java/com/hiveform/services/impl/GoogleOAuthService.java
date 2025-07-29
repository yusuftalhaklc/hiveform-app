package com.hiveform.services.impl;

import com.hiveform.config.GoogleOAuthConfig;
import com.hiveform.dto.auth.GoogleAuthRequest;
import com.hiveform.dto.auth.GoogleTokenResponse;
import com.hiveform.dto.auth.GoogleUserInfo;
import com.hiveform.dto.auth.AuthResponse;
import com.hiveform.dto.auth.GoogleAuthUrlResponse;
import com.hiveform.entities.User;
import com.hiveform.enums.AuthProvider;
import com.hiveform.enums.UserRole;
import com.hiveform.exception.ForbiddenException;
import com.hiveform.exception.UnauthorizedException;
import com.hiveform.repository.UserRepository;
import com.hiveform.security.JwtUtil;
import com.hiveform.security.JwtClaim;
import com.hiveform.services.IGoogleOAuthService;
import com.hiveform.utils.SecureTokenGenerator;
import com.hiveform.infrastructure.redis.OAuthStateRedisRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.Optional;

@Service
public class GoogleOAuthService implements IGoogleOAuthService {

    @Autowired
    private GoogleOAuthConfig googleOAuthConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SecureTokenGenerator tokenGenerator;

    @Autowired
    private OAuthStateRedisRepository oAuthStateRedisRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public GoogleAuthUrlResponse generateAuthorizationUrl() {
        String state = tokenGenerator.generateSecureToken(32);
        
        oAuthStateRedisRepository.saveState(state);
        
        String authorizationUrl = buildAuthorizationUrl(state);
        
        GoogleAuthUrlResponse response = GoogleAuthUrlResponse.builder()
            .authorizationUrl(authorizationUrl)
            .state(state)
            .build();
        
        return response;
    }

    @Override
    public AuthResponse handleOAuthCallback(String code, String state) {
        if (!oAuthStateRedisRepository.isValidState(state)) {
            throw new UnauthorizedException("Invalid or expired state parameter");
        }
        
        oAuthStateRedisRepository.deleteState(state);
        
        GoogleAuthRequest googleAuthRequestDto = GoogleAuthRequest.builder()
            .code(code)
            .state(state)
            .build();
        
        return authenticateWithGoogle(googleAuthRequestDto);
    }

    @Override
    public AuthResponse authenticateWithGoogle(GoogleAuthRequest googleAuthRequestDto) {
        try {
            GoogleTokenResponse tokenResponse = exchangeCodeForToken(googleAuthRequestDto.getCode());
            
            GoogleUserInfo userInfo = getUserInfoFromGoogle(tokenResponse.getAccessToken());
            
            User user = findOrCreateUser(userInfo);
            
            return generateAuthResponse(user);
            
        } catch (Exception e) {
            throw new UnauthorizedException("Google authentication failed: " + e.getMessage());
        }
    }

    private String buildAuthorizationUrl(String state) {
        return new StringBuilder("https://accounts.google.com/o/oauth2/v2/auth")
                .append("?client_id=").append(googleOAuthConfig.getClientId())
                .append("&redirect_uri=").append(URLEncoder.encode(googleOAuthConfig.getRedirectUri(), StandardCharsets.UTF_8))
                .append("&response_type=code")
                .append("&scope=").append(URLEncoder.encode(googleOAuthConfig.getScope(), StandardCharsets.UTF_8))
                .append("&state=").append(state)
                .append("&access_type=offline")
                .append("&prompt=consent")
                .toString();
    }

    private GoogleTokenResponse exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", googleOAuthConfig.getClientId());
        body.add("client_secret", googleOAuthConfig.getClientSecret());
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", googleOAuthConfig.getRedirectUri());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
            googleOAuthConfig.getTokenUrl(),
            request,
            GoogleTokenResponse.class
        );

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new UnauthorizedException("Failed to exchange code for token");
        }

        return response.getBody();
    }

    private GoogleUserInfo getUserInfoFromGoogle(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
            googleOAuthConfig.getUserInfoUrl(),
            HttpMethod.GET,
            request,
            GoogleUserInfo.class
        );

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new UnauthorizedException("Failed to get user info from Google");
        }

        return response.getBody();
    }

    private User findOrCreateUser(GoogleUserInfo userInfo) {
        Optional<User> userOptional = userRepository.findByProviderId(userInfo.getId());
        
        User user;
        
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            userOptional = userRepository.findByEmail(userInfo.getEmail());
            
            if (userOptional.isPresent()) {
                user = userOptional.get();
                
                if (user.getProvider() == AuthProvider.LOCAL) {
                    throw new UnauthorizedException(
                        "An account with this email already exists. Please login with your password."
                    );
                }
                
                user.setProvider(AuthProvider.GOOGLE);
                user.setProviderId(userInfo.getId());
            } else {
                user = createNewGoogleUser(userInfo);
            }
        }
        
        if (!user.getIsActive()) {
            throw new ForbiddenException("User account is not active.");
        }
        
        updateUserInfo(user, userInfo);
        userRepository.save(user);
        
        return user;
    }

    private User createNewGoogleUser(GoogleUserInfo userInfo) {
        User user = new User();
        user.setEmail(userInfo.getEmail());
        user.setFullName(userInfo.getName());
        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(userInfo.getId());
        user.setRole(UserRole.USER);
        user.setIsActive(true);
        user.setEmailVerified(true);
        
        user.setPassword("GOOGLE_AUTH_" + UUID.randomUUID().toString());
        
        return user;
    }

    private void updateUserInfo(User user, GoogleUserInfo userInfo) {
        if (!userInfo.getName().equals(user.getFullName())) {
            user.setFullName(userInfo.getName());
        }
        
        if (userInfo.isVerifiedEmail() && !user.getEmailVerified()) {
            user.setEmailVerified(true);
        }
    }

    private AuthResponse generateAuthResponse(User user) {
        JwtClaim jwtClaim = jwtUtil.createJwtClaim(
            user.getId().toString(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().name()
        );

        String accessToken = jwtUtil.generateTokenWithClaims(jwtClaim);
        String refreshToken = tokenGenerator.generateSecureToken(64);
        
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(System.currentTimeMillis() / 1000 + (30 * 24 * 60 * 60));
        userRepository.save(user);

        AuthResponse response = AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expireAt(jwtClaim.getExp())
            .build();
        
        return response;
    }
} 