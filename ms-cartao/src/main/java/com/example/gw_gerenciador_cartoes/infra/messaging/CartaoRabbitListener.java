package com.example.gw_gerenciador_cartoes.infra.messaging;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.ClienteContaCriadoDTO;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CartaoRabbitListener {

    private final CartaoServicePort service;

    public CartaoRabbitListener(CartaoServicePort service) {
        this.service = service;
    }

    @RabbitListener(queues = "${broker.queue.cartao-criar}",
            containerFactory = "rabbitListenerContainerFactory",
            errorHandler = "rabbitMqExceptionHandler")
    public void handleMensagem(ClienteContaCriadoDTO dto) {
        service.processarSolicitacao(dto);
    }
}
