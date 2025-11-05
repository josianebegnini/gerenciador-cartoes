package com.example.gw_gerenciador_cartoes.infra.messaging;

import com.example.gw_gerenciador_cartoes.application.dto.email.EmailMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${broker.exchange.email}")
    private String emailExchange;

    @Value("${broker.routing-key.email-normal}")
    private String emailNormalRoutingKey;

    @Value("${broker.routing-key.email-alta}")
    private String emailAltaRoutingKey;

    public EmailPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarEmailNormal(EmailMessageDTO dto) {
        rabbitTemplate.convertAndSend(emailExchange, emailNormalRoutingKey, dto);
    }

    public void enviarEmailAltaPrioridade(EmailMessageDTO dto) {
        rabbitTemplate.convertAndSend(emailExchange, emailAltaRoutingKey, dto);
    }

}
