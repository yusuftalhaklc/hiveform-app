package com.hiveform.services.impl;

import com.hiveform.dto.auth.RegisterRequestDto;
import com.hiveform.dto.auth.VerifyEmailRequestDto;
import com.hiveform.dto.auth.LoginRequestDto;
import com.hiveform.dto.auth.AuthResponseDto;
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

@Service
public class AuthService implements IAuthService {
    @Autowired
    private JwtUtil jwtUtil;
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail());
        if (user == null || !user.getIsActive() || !user.getEmailVerified()) {
            throw new BadCredentialsException("Kullanıcı bulunamadı veya aktif değil ya da e-posta doğrulanmamış.");
        }

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Şifre hatalı.");
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

        AuthResponseDto response = new AuthResponseDto();
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
    public String register(RegisterRequestDto registerRequestDto) {
        if (userRepository.findByEmail(registerRequestDto.getEmail()) != null) {
            throw new RuntimeException("Bu email ile kayıtlı bir kullanıcı zaten var.");
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

        return "Kullanıcı başarıyla oluşturuldu. Lütfen e-posta adresinizi doğrulayın.";
    }

    @Override
    public String verifyEmail(VerifyEmailRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {
            return "Kullanıcı bulunamadı.";
        }
        if (user.getEmailVerificationCode() == null || !user.getEmailVerificationCode().equals(dto.getCode())) {
            return "Doğrulama kodu hatalı.";
        }

        user.setEmailVerified(true);
        user.setEmailVerificationCode(null);
        userRepository.save(user);
        return "E-posta başarıyla doğrulandı.";
    }
}
