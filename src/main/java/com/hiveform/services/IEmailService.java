package com.hiveform.services;

public interface IEmailService {
    void sendVerificationEmail(String to, String verificationCode);
}
