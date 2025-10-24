package com.example.gw_gerenciador_cartoes.infra.messaging;

import com.example.gw_gerenciador_cartoes.service.CartaoService;
import com.example.gw_gerenciador_cartoes.application.dto.ClienteContaCriadoDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CartaoRabbitListener {

    private final CartaoService service;

    public CartaoRabbitListener(CartaoService service) {
        this.service = service;
    }

    @RabbitListener(queues = "cliente-conta-criado")
    public void handleMensagem(ClienteContaCriadoDTO dto) {
        Long clienteId = dto.getClienteId();
        service.gerar(clienteId);
    }

}
