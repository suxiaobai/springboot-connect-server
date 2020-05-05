package com.easemob.serversslconnection.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Sender {

    @Autowired
    private volatile RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitConfiguration rabbitConfiguration;

    private final AtomicInteger counter = new AtomicInteger();

    @Scheduled(fixedRate = 3000)
    public void sendMessage() {
        String message = "Hello World " + counter.incrementAndGet();
        rabbitTemplate.convertAndSend(rabbitConfiguration.topicExchangeName, rabbitConfiguration.routingKey, message);
        System.out.println("Send <" + message + ">");
    }
}