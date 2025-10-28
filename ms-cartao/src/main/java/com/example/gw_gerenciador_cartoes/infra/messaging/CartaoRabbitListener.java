package com.example.gw_gerenciador_cartoes.infra.messaging;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.CriarCartaoMessageDTO;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CartaoRabbitListener {

    private static final Logger log = LoggerFactory.getLogger(CartaoRabbitListener.class);
    private final CartaoServicePort service;
    private final ObjectMapper objectMapper;
//    private final ExceptionHandlerService exceptionHandlerService;

    public CartaoRabbitListener(CartaoServicePort service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${broker.queue.cartao-criar}",
            containerFactory = "rabbitListenerContainerFactory",
            errorHandler = "rabbitMqExceptionHandler")
    public void handleMensagem(CriarCartaoMessageDTO dto) {
        Integer idSolicitacao = null;
//        try {
            service.processarSolicitacao(dto);
        //    idSolicitacao = cartao.getIdSolicitacao();
//        } catch (Exception e) {
        //    exceptionHandlerService.tratarExcecao(e, idSolicitacao);
//            log.error("Erro ao processar mensagem: {}", dto, e);
//            throw new AmqpRejectAndDontRequeueException("Erro ao processar mensagem", e);
//        }
    }
}
