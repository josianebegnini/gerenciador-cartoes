package com.example.gw_gerenciador_cartoes.infra.exception;

import com.example.gw_gerenciador_cartoes.domain.ports.SolicitacaoCartaoServicePort;
import com.example.gw_gerenciador_cartoes.infra.converter.StringConverter;
import com.example.gw_gerenciador_cartoes.service.SolicitacaoCartaoService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("rabbitMqExceptionHandler")
public class RabbitMqExceptionHandler implements RabbitListenerErrorHandler {

    private final SolicitacaoCartaoServicePort solicitacaoCartaoService;

    public RabbitMqExceptionHandler(SolicitacaoCartaoServicePort solicitacaoCartaoService) {
        this.solicitacaoCartaoService = solicitacaoCartaoService;
    }

    @Override
    public Object handleError(Message amqpMessage,
                              Channel channel,
                              org.springframework.messaging.Message<?> message,
                              ListenerExecutionFailedException exception) throws Exception {

        Throwable causa = exception.getCause();

        if (causa instanceof CampoObrigatorioException ex) {
            String motivo = ex.getMensagensErros().stream()
                    .map(MensagemErro::getMensagem)
                    .collect(Collectors.joining(" | "));

            solicitacaoCartaoService.rejeitarSolicitacao(ex.getSolicitacaoId(), motivo, message.toString());
        }
        return null;
    }
}
