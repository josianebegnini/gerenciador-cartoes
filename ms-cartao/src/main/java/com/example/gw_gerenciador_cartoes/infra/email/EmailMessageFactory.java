package com.example.gw_gerenciador_cartoes.infra.email;

import com.example.gw_gerenciador_cartoes.application.dto.email.EmailMessageDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class EmailMessageFactory {

    public EmailMessageDTO criarEmail(String tipo, Cartao cartao, SolicitacaoCartao solicitacao, Map<String, String> dados) {
        EmailMessageDTO email = new EmailMessageDTO();
        email.setTipo(tipo);
        email.setEmail(solicitacao.getEmail());
        email.setNome(solicitacao.getNome());
        email.setDados(dados);
        return email;
    }

    public String extrairFinalCartao(String numeroCartao) {
        return numeroCartao != null && numeroCartao.length() >= 4
                ? numeroCartao.substring(numeroCartao.length() - 4)
                : "****";
    }

    public String formatarData(LocalDateTime data) {
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

}