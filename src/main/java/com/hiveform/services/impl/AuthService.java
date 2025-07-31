package com.hiveform.services.impl;

import com.hiveform.dto.auth.RegisterRequest;
import com.hiveform.dto.auth.VerifyEmailRequest;
import com.hiveform.dto.auth.LoginRequest;
import com.hiveform.dto.auth.AuthResponse;
import com.hiveform.dto.auth.ForgotPasswordRequest;
import com.hiveform.dto.auth.ResetPasswordRequest;
import com.hiveform.security.JwtUtil;
import com.hiveform.security.JwtClaim;
import com.hiveform.entities.User;
import com.hiveform.exception.ForbiddenException;
import com.hiveform.exception.ResourceNotFoundException;
import com.hiveform.exception.UnauthorizedException;
import com.hiveform.repository.UserRepository;
import com.hiveform.services.IAuthService;
import com.hiveform.services.IEmailService;
import com.hiveform.infrastructure.redis.PasswordResetRedisRepository;
import com.hiveform.utils.SecureTokenGenerator;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private PasswordResetRedisRepository passwordResetRedisRepository;

    @Autowired
    private SecureTokenGenerator tokenGenerator;

    @Override
    public AuthResponse login(LoginRequest loginRequestDto) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequestDto.getEmail());

        if (optionalUser.isEmpty() || !passwordEncoder.matches(loginRequestDto.getPassword(), optionalUser.get().getPassword())) {
            throw new UnauthorizedException("Email or password is incorrect.");
        }

        User user = optionalUser.get();

        if (!user.getIsActive()) {
            throw new ForbiddenException("User account is not active.");
        }

        if (!user.getEmailVerified()) {
            throw new ForbiddenException("Email address is not verified.");
        }

        if (user.getProvider() != com.hiveform.enums.AuthProvider.LOCAL) {
            throw new UnauthorizedException("Please use " + user.getProvider().name().toLowerCase() + " authentication.");
        }

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

    @Override
    public void register(RegisterRequest registerRequestDto) {
        Optional<User> optionalUser = userRepository.findByEmail(registerRequestDto.getEmail());

        if (optionalUser.isPresent()) {
            throw new UnauthorizedException("A user with this email already exists.");
        }

        User user = new User();
        BeanUtils.copyProperties(registerRequestDto, user);
        user.setEmail(registerRequestDto.getEmail());
        user.setFullName(registerRequestDto.getFullName());
        user.setEmailVerified(false);
        user.setProvider(com.hiveform.enums.AuthProvider.LOCAL);
        user.setProviderId(null);
        user.setRole(com.hiveform.enums.UserRole.USER);
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

        String verificationCode = tokenGenerator.generateSecureToken(32);
        user.setEmailVerificationCode(verificationCode);

        userRepository.save(user);

        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationCode);
        } catch (Exception e) {
            e.printStackTrace();
            // log.error("Doğrulama maili gönderilemedi", e);
        }
    }

    @Override
    public void verifyEmail(VerifyEmailRequest dto) {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        User user = optionalUser.get();

        if (user.getEmailVerificationCode() == null || !user.getEmailVerificationCode().equals(dto.getCode())) {
            throw new UnauthorizedException("Invalid verification code.");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationCode(null);
        userRepository.save(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequestDto) {
        Optional<User> optionalUser = userRepository.findByEmail(forgotPasswordRequestDto.getEmail());
        
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        User user = optionalUser.get();

        if (!user.getIsActive()) {
            throw new ForbiddenException("User account is not active.");
        }

        if (passwordResetRedisRepository.hasResetToken(forgotPasswordRequestDto.getEmail())) {
            throw new UnauthorizedException("A password reset email has already been sent. Please check your email or wait before requesting another.");
        }

        String resetToken = tokenGenerator.generateSecureToken(64);
        
        passwordResetRedisRepository.saveResetToken(forgotPasswordRequestDto.getEmail(), resetToken);
        
        emailService.sendPasswordResetEmail(forgotPasswordRequestDto.getEmail(), resetToken);
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequestDto) {
        if (!passwordResetRedisRepository.isValidToken(resetPasswordRequestDto.getToken())) {
            throw new UnauthorizedException("Invalid or expired reset token.");
        }

        String email = passwordResetRedisRepository.getEmailByToken(resetPasswordRequestDto.getToken());
        if (email == null) {
            throw new UnauthorizedException("Invalid reset token.");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        User user = optionalUser.get();

        if (!user.getIsActive()) {
            throw new ForbiddenException("User account is not active.");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        userRepository.save(user);
        
        passwordResetRedisRepository.deleteResetToken(email);
    }
}
