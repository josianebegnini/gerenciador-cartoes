package com.example.gw_gerenciador_cartoes.infra.messaging;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.ClienteContaCriadoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CartaoDlqListener {

    private static final Logger log = LoggerFactory.getLogger(CartaoDlqListener.class);
    private final ObjectMapper objectMapper;

    public CartaoDlqListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${broker.queue.cartao-dlq}")
    public void handleMensagemRejeitada(String payload) {
        try {
            ClienteContaCriadoDTO dto = objectMapper.readValue(payload, ClienteContaCriadoDTO.class);
            log.warn("Mensagem rejeitada recebida na DLQ: {}", dto);
        } catch (Exception e) {
            log.error("Erro ao processar mensagem da DLQ: {}", payload, e);
        }
    }

}
