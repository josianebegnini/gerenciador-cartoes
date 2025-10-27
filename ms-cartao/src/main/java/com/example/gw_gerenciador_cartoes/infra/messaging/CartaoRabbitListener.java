package com.example.gw_gerenciador_cartoes.infra.messaging;

import com.example.gw_gerenciador_cartoes.application.dto.CriarCartaoMessageDTO;
import com.example.gw_gerenciador_cartoes.service.CartaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CartaoRabbitListener {

    private static final Logger log = LoggerFactory.getLogger(CartaoRabbitListener.class);
    private final CartaoService service;
    private final ObjectMapper objectMapper;

    public CartaoRabbitListener(CartaoService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${broker.queue.cartao-criar}", containerFactory = "rabbitListenerContainerFactory")
    public void handleMensagem(CriarCartaoMessageDTO dto) {
        try {
            service.gerar(dto);
        } catch (Exception e) {
            log.error("Erro ao processar mensagem: {}", dto, e);
            throw new AmqpRejectAndDontRequeueException("Erro ao processar mensagem", e);
        }

    }
}
