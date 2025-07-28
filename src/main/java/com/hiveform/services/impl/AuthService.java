package com.hiveform.services.impl;

import com.hiveform.dto.auth.DtoRegisterIU;
import com.hiveform.dto.auth.DtoVerifyEmailIU;
import com.hiveform.dto.auth.DtoLoginIU;
import com.hiveform.dto.auth.DtoAuthResponse;
import com.hiveform.security.JwtUtil;
import com.hiveform.security.JwtClaim;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;
import com.hiveform.entities.User;
import com.hiveform.repository.UserRepository;
import com.hiveform.services.IAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import com.hiveform.handler.ResourceNotFoundException;
import com.hiveform.handler.UnauthorizedException;
import com.hiveform.handler.ForbiddenException;

@Service
public class AuthService implements IAuthService {
    @Autowired
    private JwtUtil jwtUtil;
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @Override
    public DtoAuthResponse login(DtoLoginIU loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail());

        if (user == null || !passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Email or password is incorrect.");
        }
        if (!user.getIsActive()) {
            throw new ForbiddenException("User account is not active.");
        }
        if (!user.getEmailVerified()) {
            throw new ForbiddenException("Email address is not verified.");
        }

        JwtClaim claim = JwtClaim.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .fullname(user.getFullName())
                .role(user.getRole().name())
                .iss("hiveform")
                .iat(System.currentTimeMillis() / 1000)
                .exp((System.currentTimeMillis() + jwtExpiration) / 1000)
                .build();

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", claim.getUserId());
        claims.put("email", claim.getEmail());
        claims.put("fullname", claim.getFullname());
        claims.put("role", claim.getRole());
        claims.put("iss", claim.getIss());
        claims.put("iat", claim.getIat());
        claims.put("exp", claim.getExp());

        String accessToken = jwtUtil.generateToken(claims, user.getEmail(), jwtExpiration);

        String refreshToken = generateSecureToken(64);
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(30));
        userRepository.save(user);

        DtoAuthResponse response = new DtoAuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpireAt(claim.getExp());
        return response;

    }

    private String generateSecureToken(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder token = new StringBuilder(length);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            token.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return token.toString();
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public String register(DtoRegisterIU registerRequestDto) {
        if (userRepository.findByEmail(registerRequestDto.getEmail()) != null) {
            throw new UnauthorizedException("A user with this email already exists.");
        }

        User user = new User();
        BeanUtils.copyProperties(registerRequestDto, user);
        user.setEmail(registerRequestDto.getEmail());
        user.setFullName(registerRequestDto.getFullName());
        user.setEmailVerified(false);
        user.setProvider(com.hiveform.enums.AuthProvider.LOCAL);
        user.setProviderId(null);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        user.setRole(com.hiveform.enums.UserRole.USER);
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

        String verificationCode = UUID.randomUUID().toString().replace("-", "");
        user.setEmailVerificationCode(verificationCode);

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationCode);

        return "User registered successfully. Please verify your email address.";
    }

    @Override
    public String verifyEmail(DtoVerifyEmailIU dto) {
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("User not found.");
        }
        if (user.getEmailVerificationCode() == null || !user.getEmailVerificationCode().equals(dto.getCode())) {
            throw new UnauthorizedException("Invalid verification code.");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationCode(null);
        userRepository.save(user);
        return "Email verified successfully.";
    }
}
