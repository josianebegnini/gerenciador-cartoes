package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.application.dto.email.EmailMessageDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.infra.messaging.EmailPublisher;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class CartaoEmailService {

    private final EmailPublisher emailPublisher;
    private final SolicitacaoCartaoService solicitacaoCartaoService;

    public CartaoEmailService(EmailPublisher emailPublisher, SolicitacaoCartaoService solicitacaoCartaoService) {
        this.emailPublisher = emailPublisher;
        this.solicitacaoCartaoService = solicitacaoCartaoService;
    }

    public void enviarEmailCartaoCriado(Cartao cartao) {
        if (!podeEnviarEmail(cartao)) return;

        String numeroMascarado = "**** **** **** " + extrairFinalCartao(cartao.getNumero());
        Map<String, String> dados = Map.of(
                "numeroCartao", numeroMascarado,
                "dataEmissao", cartao.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        enviarEmail("cartao-criado", cartao, dados, false);
    }

    public void enviarEmailCartaoAtivado(Cartao cartao) {
        if (!podeEnviarEmail(cartao)) return;

        Map<String, String> dados = Map.of(
                "finalCartao", extrairFinalCartao(cartao.getNumero())
        );
        enviarEmail("cartao-ativo", cartao, dados, false);
    }

    public void enviarEmailCartaoBloqueado(Cartao cartao, String motivo) {
        if (!podeEnviarEmail(cartao)) return;

        Map<String, String> dados = Map.of(
                "finalCartao", extrairFinalCartao(cartao.getNumero()),
                "dataBloqueio", cartao.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        enviarEmail("cartao-bloqueado", cartao, dados, true);
    }

    public void enviarEmailSegundaVia(Cartao cartao) {
        if (!podeEnviarEmail(cartao)) return;

        Map<String, String> dados = Map.of(
                "finalCartao", extrairFinalCartao(cartao.getNumero()),
                "dataEmissao", cartao.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                "previsaoEntrega", "7 dias úteis"
        );
        enviarEmail("segunda-via", cartao, dados, false);
    }

    private void enviarEmail(String tipo, Cartao cartao, Map<String, String> dados, boolean altaPrioridade) {

        SolicitacaoCartao solicitacaoCartao = buscarNomeEmailPorSolicitacao(cartao.getSolicitacaoId());

        EmailMessageDTO email = new EmailMessageDTO();
        email.setTipo(tipo);
        email.setEmail(solicitacaoCartao.getEmail());
        email.setNome(solicitacaoCartao.getNome());
        email.setDados(dados);

        if (altaPrioridade) {
            emailPublisher.enviarEmailAltaPrioridade(email);
        } else {
            emailPublisher.enviarEmailNormal(email);
        }
    }

    private boolean podeEnviarEmail(Cartao cartao) {
        return cartao.getSolicitacaoId() != null && cartao.getSolicitacaoId() != 0;
    }

    private String extrairFinalCartao(String numeroCartao) {
        return numeroCartao != null && numeroCartao.length() >= 4
                ? numeroCartao.substring(numeroCartao.length() - 4)
                : "****";
    }

    private SolicitacaoCartao buscarNomeEmailPorSolicitacao(Long solicitacaoId) {
        return solicitacaoCartaoService.buscarPorId(solicitacaoId);
    }

}
