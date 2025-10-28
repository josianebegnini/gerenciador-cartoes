package com.example.gw_gerenciador_cartoes.infra.messaging;

import com.example.gw_gerenciador_cartoes.domain.ports.EmailNormalQueueProducerPort;
import com.example.gw_gerenciador_cartoes.infra.config.RabbitMQConfig;
import com.example.gw_gerenciador_cartoes.infra.dto.EmailMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailNormalQueueProducer implements EmailNormalQueueProducerPort {

    private final RabbitTemplate rabbitTemplate;

    public EmailNormalQueueProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmailNormalQueue(EmailMessageDTO emailMessageDTO) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.ROUTING_KEY_EMAIL_NORMAL_QUEUE,
                emailMessageDTO
        );
    }
}