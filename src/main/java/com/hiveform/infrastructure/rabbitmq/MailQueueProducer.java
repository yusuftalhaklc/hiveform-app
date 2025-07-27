package com.hiveform.infrastructure.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailQueueProducer {
   
    public static final String MAIL_QUEUE = "mail.queue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMail(String mailRequest) {
        rabbitTemplate.convertAndSend(MAIL_QUEUE, mailRequest);
    }
}
