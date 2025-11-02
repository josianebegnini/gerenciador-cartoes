package com.example.gw_gerenciador_cartoes.infra.email;

import com.example.gw_gerenciador_cartoes.application.dto.email.EmailMessageDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.infra.messaging.EmailPublisher;
import com.example.gw_gerenciador_cartoes.service.SolicitacaoCartaoService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CartaoEmailService {

    private final EmailPublisher emailPublisher;
    private final SolicitacaoCartaoService solicitacaoCartaoService;
    private final EmailMessageFactory emailMessageFactory;

    public CartaoEmailService(EmailPublisher emailPublisher, SolicitacaoCartaoService solicitacaoCartaoService, EmailMessageFactory emailMessageFactory) {
        this.emailPublisher = emailPublisher;
        this.solicitacaoCartaoService = solicitacaoCartaoService;
        this.emailMessageFactory = emailMessageFactory;
    }

    public void enviarEmailCartaoCriado(Cartao cartao) {
        if (!podeEnviarEmail(cartao)) return;

        SolicitacaoCartao solicitacao = solicitacaoCartaoService.buscarPorId(cartao.getSolicitacaoId());

        Map<String, String> dados = Map.of(
                "numeroCartao", "**** **** **** " + emailMessageFactory.extrairFinalCartao(cartao.getNumero()),
                "dataEmissao", emailMessageFactory.formatarData(cartao.getDataCriacao())
        );

        EmailMessageDTO email = emailMessageFactory.criarEmail("cartao-criado", cartao, solicitacao, dados);
        emailPublisher.enviarEmailNormal(email);
    }


    public void enviarEmailCartaoAtivado(Cartao cartao) {
        if (!podeEnviarEmail(cartao)) return;

        SolicitacaoCartao solicitacao = solicitacaoCartaoService.buscarPorId(cartao.getSolicitacaoId());

        Map<String, String> dados = Map.of(
                "finalCartao", emailMessageFactory.extrairFinalCartao(cartao.getNumero())
        );

        EmailMessageDTO email = emailMessageFactory.criarEmail("cartao-ativo", cartao, solicitacao, dados);
        emailPublisher.enviarEmailNormal(email);
    }

    public void enviarEmailCartaoBloqueado(Cartao cartao, String motivo) {
        if (!podeEnviarEmail(cartao)) return;

        SolicitacaoCartao solicitacao = solicitacaoCartaoService.buscarPorId(cartao.getSolicitacaoId());

        Map<String, String> dados = Map.of(
                "finalCartao", emailMessageFactory.extrairFinalCartao(cartao.getNumero()),
                "dataBloqueio", emailMessageFactory.formatarData(cartao.getDataCriacao())
        );

        EmailMessageDTO email = emailMessageFactory.criarEmail("cartao-bloqueado", cartao, solicitacao, dados);
        emailPublisher.enviarEmailAltaPrioridade(email);
    }

    public void enviarEmailSegundaVia(Cartao cartao) {
        if (!podeEnviarEmail(cartao)) return;

        SolicitacaoCartao solicitacao = solicitacaoCartaoService.buscarPorId(cartao.getSolicitacaoId());

        Map<String, String> dados = Map.of(
                "finalCartao", emailMessageFactory.extrairFinalCartao(cartao.getNumero()),
                "dataEmissao", emailMessageFactory.formatarData(cartao.getDataCriacao()),
                "previsaoEntrega", "7 dias úteis"
        );

        EmailMessageDTO email = emailMessageFactory.criarEmail("segunda-via", cartao, solicitacao, dados);
        emailPublisher.enviarEmailNormal(email);
    }

    private boolean podeEnviarEmail(Cartao cartao) {
        return cartao.getSolicitacaoId() != null && cartao.getSolicitacaoId() != 0;
    }

}
