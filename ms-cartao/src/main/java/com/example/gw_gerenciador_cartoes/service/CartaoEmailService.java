package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.application.dto.email.EmailMessageDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.infra.messaging.EmailPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class CartaoEmailService {

    private final EmailPublisher emailPublisher;

    public CartaoEmailService(EmailPublisher emailPublisher) {
        this.emailPublisher = emailPublisher;
    }

    public void enviarEmailCartaoCriado(Cartao cartao) {
        EmailMessageDTO email = new EmailMessageDTO();
        email.setTipo("cartao-criado");
        email.setEmail(cartao.getEmail());
        email.setNome(cartao.getNome());
        email.setDados(Map.of(
                "numeroCartao", cartao.getNumero(),
                "dataCriacao", cartao.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ));

        emailPublisher.enviarEmailNormal(email);
    }

    public void enviarEmailCartaoAtivado(Cartao cartao) {
        EmailMessageDTO email = new EmailMessageDTO();
        email.setTipo("cartao-ativo");
        email.setEmail(cartao.getEmail());
        email.setNome(cartao.getNome());
        email.setDados(Map.of(
                "numeroCartao", cartao.getNumero(),
                "dataAtivacao", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ));

        emailPublisher.enviarEmailNormal(email);
    }

    public void enviarEmailCartaoBloqueado(Cartao cartao, String motivo) {
        EmailMessageDTO email = new EmailMessageDTO();
        email.setTipo("cartao-bloqueado");
        email.setEmail(cartao.getEmail());
        email.setNome(cartao.getNome());
        email.setDados(Map.of(
                "numeroCartao", cartao.getNumero(),
                "motivo", motivo
        ));

        emailPublisher.enviarEmailAltaPrioridade(email);
    }

    public void enviarEmailSegundaVia(Cartao novaVia) {
        EmailMessageDTO email = new EmailMessageDTO();
        email.setTipo("segunda-via");
        email.setEmail(novaVia.getEmail());
        email.setNome(novaVia.getNome());
        email.setDados(Map.of(
                "numeroCartao", novaVia.getNumero(),
                "prazoEntrega", "7 dias úteis"
        ));

        emailPublisher.enviarEmailNormal(email);
    }

}
