package com.example.gw_gerenciador_cartoes.infra.messaging;

import com.example.gw_gerenciador_cartoes.application.dto.CriarCartaoMessageDTO;
import com.example.gw_gerenciador_cartoes.service.CartaoRespostaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CartaoDlqListener {

    private static final Logger log = LoggerFactory.getLogger(CartaoDlqListener.class);
    private final ObjectMapper objectMapper;


    private final CartaoRespostaService rejeitadoService;

    public CartaoDlqListener(ObjectMapper objectMapper, CartaoRespostaService rejeitadoService) {
        this.objectMapper = objectMapper;
        this.rejeitadoService = rejeitadoService;
    }

    @RabbitListener(queues = "${broker.queue.cartao-dlq}")
    public void handleMensagemRejeitada(String payload) {
        try {
            CriarCartaoMessageDTO dto = objectMapper.readValue(payload, CriarCartaoMessageDTO.class);
            log.warn("Mensagem rejeitada recebida na DLQ: {}", dto);
        } catch (Exception e) {
            log.error("Erro ao processar mensagem da DLQ: {}", payload, e);
        }
    }

}
