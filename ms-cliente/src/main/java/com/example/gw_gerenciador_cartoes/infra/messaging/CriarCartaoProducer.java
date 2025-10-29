package com.example.gw_gerenciador_cartoes.infra.messaging;

import com.example.gw_gerenciador_cartoes.domain.ports.CriarCartaoProducerPort;
import com.example.gw_gerenciador_cartoes.infra.config.RabbitMQConfig;
import com.example.gw_gerenciador_cartoes.infra.dto.ClienteContaCriadoDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CriarCartaoProducer implements CriarCartaoProducerPort {

    private final RabbitTemplate rabbitTemplate;

    public CriarCartaoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCriarCartao(ClienteContaCriadoDTO clienteContaCriadoDTO) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_CARTAO,
            RabbitMQConfig.ROUTING_KEY_CRIAR_CARTAO,
            clienteContaCriadoDTO
        );
    }
}