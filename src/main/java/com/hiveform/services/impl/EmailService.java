package com.hiveform.services.impl;

import com.hiveform.services.IEmailService;
import com.hiveform.infrastructure.rabbitmq.MailQueueProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {

    @Autowired
    private MailQueueProducer mailQueueProducer;

    @Override
    public void sendVerificationEmail(String to, String verificationCode) {
        String subject = "Email Doğrulama Kodu";
        String text = "Doğrulama kodunuz: " + verificationCode;
        String mailRequest = to + "|" + subject + "|" + text;
        mailQueueProducer.sendMail(mailRequest);
    }
}
