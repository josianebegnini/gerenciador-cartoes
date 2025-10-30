package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.application.dto.email.EmailMessageDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.infra.messaging.EmailPublisher;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class CartaoEmailService {

    private final EmailPublisher emailPublisher;

    public CartaoEmailService(EmailPublisher emailPublisher) {
        this.emailPublisher = emailPublisher;
    }

    public void enviarEmailCartaoCriado(Cartao cartao) {

        String numeroCartao = cartao.getNumero();
        String numeroMascarado = "**** **** **** " + extrairFinalCartao(numeroCartao);

        EmailMessageDTO email = new EmailMessageDTO();
        email.setTipo("cartao-criado");
        email.setEmail(cartao.getEmail());
        email.setNome(cartao.getNome());
        email.setDados(Map.of(
                "numeroCartao", numeroMascarado,
                "dataEmissao", cartao.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        );

        emailPublisher.enviarEmailNormal(email);
    }

    public void enviarEmailCartaoAtivado(Cartao cartao) {
        String finalCartao = extrairFinalCartao(cartao.getNumero());

        EmailMessageDTO email = new EmailMessageDTO();
        email.setTipo("cartao-ativo");
        email.setEmail(cartao.getEmail());
        email.setNome(cartao.getNome());
        email.setDados(Map.of(
                "finalCartao", finalCartao)
        );

        emailPublisher.enviarEmailNormal(email);
    }

    public void enviarEmailCartaoBloqueado(Cartao cartao, String motivo) {
        String finalCartao = extrairFinalCartao(cartao.getNumero());

        EmailMessageDTO email = new EmailMessageDTO();
        email.setTipo("cartao-bloqueado");
        email.setEmail(cartao.getEmail());
        email.setNome(cartao.getNome());
        email.setDados(Map.of(
                "finalCartao", finalCartao,
                "dataBloqueio",cartao.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        );

        emailPublisher.enviarEmailAltaPrioridade(email);
    }

    public void enviarEmailSegundaVia(Cartao cartao) {
        String finalCartao = extrairFinalCartao(cartao.getNumero());

        EmailMessageDTO email = new EmailMessageDTO();
        email.setTipo("segunda-via");
        email.setEmail(cartao.getEmail());
        email.setNome(cartao.getNome());
        email.setDados(Map.of(
                "finalCartao", finalCartao,
                "dataEmissao", cartao.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                "previsaoEntrega", "7 dias úteis"
        ));

        emailPublisher.enviarEmailNormal(email);
    }

    private String extrairFinalCartao(String numeroCartao) {
        return numeroCartao != null && numeroCartao.length() >= 4
                ? numeroCartao.substring(numeroCartao.length() - 4)
                : "****";
    }

}
