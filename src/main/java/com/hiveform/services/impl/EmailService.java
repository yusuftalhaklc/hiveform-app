package com.hiveform.services.impl;

import com.hiveform.services.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendVerificationEmail(String to, String verificationCode) {
        String subject = "Email Doğrulama Kodu";
        String text = "Doğrulama kodunuz: " + verificationCode;
        sendMail(to, subject, text);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "Şifre Sıfırlama";
        String text = "Şifrenizi sıfırlamak için aşağıdaki kodu kullanın: " + resetToken + "\n\nBu kod 3 dakika geçerlidir.";
        sendMail(to, subject, text);
    }

    @Async
    public void sendMail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // Logla veya gerekirse tekrar dene
            e.printStackTrace();
        }
    }
}
