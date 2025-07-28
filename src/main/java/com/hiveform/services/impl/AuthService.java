package com.hiveform.services.impl;

import com.hiveform.dto.auth.DtoRegisterIU;
import com.hiveform.dto.auth.DtoVerifyEmailIU;
import com.hiveform.dto.auth.DtoLoginIU;
import com.hiveform.dto.auth.DtoAuthResponse;
import com.hiveform.dto.auth.DtoForgotPasswordIU;
import com.hiveform.dto.auth.DtoResetPasswordIU;
import com.hiveform.security.JwtUtil;
import com.hiveform.security.JwtClaim;
import com.hiveform.entities.User;
import com.hiveform.repository.UserRepository;
import com.hiveform.services.IAuthService;
import com.hiveform.services.IEmailService;
import com.hiveform.infrastructure.redis.PasswordResetRedisRepository;
import com.hiveform.utils.SecureTokenGenerator;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.hiveform.handler.ResourceNotFoundException;
import com.hiveform.handler.UnauthorizedException;
import com.hiveform.handler.ForbiddenException;

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

        DtoAuthResponse response = new DtoAuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpireAt(jwtClaim.getExp());
        return response;

    }

    @Override
    public void register(DtoRegisterIU registerRequestDto) {
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
        user.setRole(com.hiveform.enums.UserRole.USER);
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

        String verificationCode = tokenGenerator.generateSecureToken(64);
        user.setEmailVerificationCode(verificationCode);

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationCode);
    }

    @Override
    public void verifyEmail(DtoVerifyEmailIU dto) {
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
    }

    @Override
    public void forgotPassword(DtoForgotPasswordIU forgotPasswordRequestDto) {
        User user = userRepository.findByEmail(forgotPasswordRequestDto.getEmail());
        
        if (user == null) {
            throw new ResourceNotFoundException("User not found.");
        }

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
    public void resetPassword(DtoResetPasswordIU resetPasswordRequestDto) {
        if (!passwordResetRedisRepository.isValidToken(resetPasswordRequestDto.getToken())) {
            throw new UnauthorizedException("Invalid or expired reset token.");
        }

        String email = passwordResetRedisRepository.getEmailByToken(resetPasswordRequestDto.getToken());
        if (email == null) {
            throw new UnauthorizedException("Invalid reset token.");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found.");
        }

        if (!user.getIsActive()) {
            throw new ForbiddenException("User account is not active.");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        userRepository.save(user);
        
        passwordResetRedisRepository.deleteResetToken(email);
    }
}
