package com.fhs.vibesense.rmq;

import com.fhs.vibesense.data.Event;
import com.fhs.vibesense.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!no_rabbit")
public class RabbitMQReceiver {

    private final EventService eventService;

    private final AmqpTemplate amqpTemplate;

    @Autowired
    public RabbitMQReceiver(EventService eventService, AmqpTemplate amqpTemplate) {
        this.eventService = eventService;
        this.amqpTemplate = amqpTemplate;
    }

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory", queues = "${spring.rabbitmq.queueName}")
    public void receiveMessage(Event event) {
        log.info(event);
        eventService.processEvent(event);
    }

    public void sendMessage(Event event) {
        amqpTemplate.convertAndSend(event);
    }
}
