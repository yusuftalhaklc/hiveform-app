package com.hiveform.infrastructure.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MailQueueConsumer {
    @RabbitListener(queues = MailQueueProducer.MAIL_QUEUE)
    public void consumeMail(String mailRequest) {
        System.out.println("Mail kuyruğundan mesaj alındı: " + mailRequest);
    }
}
