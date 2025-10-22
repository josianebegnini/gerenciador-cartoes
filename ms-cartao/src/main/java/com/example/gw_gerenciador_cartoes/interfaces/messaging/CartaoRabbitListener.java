package com.example.gw_gerenciador_cartoes.interfaces.messaging;

import com.example.gw_gerenciador_cartoes.application.service.CartaoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CartaoRabbitListener {

    private final CartaoService service;

    public CartaoRabbitListener(CartaoService service) {
        this.service = service;
    }

    @RabbitListener(queues = "cliente-conta-criado")
    public void receberMensagem(String mensagem) {
        String[] dados = mensagem.split(",");
        String clienteId = dados[0];
        String contaId = dados[1];
        service.gerarCartao(clienteId, contaId);
    }

}
