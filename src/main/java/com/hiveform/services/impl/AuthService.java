package com.hiveform.services.impl;

import com.hiveform.dto.auth.RegisterRequestDto;
import com.hiveform.dto.auth.VerifyEmailRequestDto;
//import com.hiveform.dto.auth.AuthResponseDto;
import com.hiveform.entities.User;
import com.hiveform.repository.UserRepository;
import com.hiveform.services.IAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Service
public class AuthService implements IAuthService {

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
