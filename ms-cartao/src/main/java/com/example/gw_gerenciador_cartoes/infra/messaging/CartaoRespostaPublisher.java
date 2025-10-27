package com.example.gw_gerenciador_cartoes.infra.messaging;

import com.example.gw_gerenciador_cartoes.application.dto.CriarCartaoResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CartaoRespostaPublisher {

    private static final Logger log = LoggerFactory.getLogger(CartaoRespostaPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public CartaoRespostaPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarResposta(CriarCartaoResponseDTO resposta) {
        log.info("📤 Publicando resposta: clienteId={}, contaId={}, sucesso={}, mensagem={}",
                resposta.getClienteId(), resposta.getContaId(), resposta.isSucesso(), resposta.getMensagem());

        rabbitTemplate.convertAndSend("cartao-resposta-exchange", "cartao.criar.resposta", resposta);
    }

}
