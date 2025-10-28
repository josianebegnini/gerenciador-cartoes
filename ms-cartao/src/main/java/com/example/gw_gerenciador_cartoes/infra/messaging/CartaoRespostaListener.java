package com.example.gw_gerenciador_cartoes.infra.messaging;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.CriarCartaoResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CartaoRespostaListener {

    private static final Logger log = LoggerFactory.getLogger(CartaoRespostaListener.class);

    @RabbitListener(queues = "cartao-criar-resposta-queue", containerFactory = "rabbitListenerContainerFactory")
    public void handleResposta(CriarCartaoResponseDTO response) {
        System.out.println("📥 Payload recebido: " + response);
        if (response.isSucesso()) {
            log.info("✅ Cartão criado com sucesso para clienteId {} e contaId {}. Mensagem: {}",
                    response.getClienteId(), response.getContaId(), response.getMensagem());
        } else {
            log.warn("❌ Falha ao criar cartão para clienteId {} e contaId {}. Motivo: {}",
                    response.getClienteId(), response.getContaId(), response.getMensagem());
        }
    }
}