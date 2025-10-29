package com.example.gw_gerenciador_cartoes.infra.exception;

import com.example.gw_gerenciador_cartoes.domain.ports.SolicitacaoCartaoServicePort;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

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


        } else if (causa instanceof RegraNegocioException ex) {
            String motivo = ex.getMessage();
            System.err.println(MensagensErroConstantes.ERRO_REGRA_NEGOCIO_LOG + motivo);
            throw new ListenerExecutionFailedException(MensagensErroConstantes.ERRO_REGRA_NEGOCIO_SEM_ID, causa);

        } else {
            System.err.println(MensagensErroConstantes.ERRO_INESPERADO_LOG + causa.getMessage());
            throw new ListenerExecutionFailedException(MensagensErroConstantes.ERRO_PROCESSAMENTO_MENSAGEM, causa);
        }

        return null;
    }
}
