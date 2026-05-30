package com.rabbitmail.producer;

import com.rabbitmail.dto.EmailQueueMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmail.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmail.rabbitmq.routing-key}")
    private String routingKey;

    public void publishEmailRequest(EmailQueueMessage message) {
        log.info("Publishing email request to RabbitMQ. messageId={}, batchSendId={}",
                message.getMessageId(), message.getBatchSendId());

        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }
}
