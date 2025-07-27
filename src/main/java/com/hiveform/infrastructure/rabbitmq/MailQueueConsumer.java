package com.hiveform.infrastructure.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiveform.dto.MailDto;

@Component
public class MailQueueConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(queues = MailQueueProducer.MAIL_QUEUE)
    public void consumeMail(String mailRequest) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MailDto mailDto = objectMapper.readValue(mailRequest, MailDto.class);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailDto.getFrom());
            message.setTo(mailDto.getTo());
            message.setSubject(mailDto.getSubject());
            message.setText(mailDto.getText());
            mailSender.send(message);
        } catch (Exception e) {
           
        }
    }
}
