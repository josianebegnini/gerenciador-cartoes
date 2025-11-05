package com.example.gw_gerenciador_cartoes.infra.email;

import com.example.gw_gerenciador_cartoes.application.dto.email.EmailMessageDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusSolicitacao;
import com.example.gw_gerenciador_cartoes.testutil.CartaoTestConstants;
import com.example.gw_gerenciador_cartoes.testutil.CartaoTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailMessageFactoryTest {

    private final EmailMessageFactory factory = new EmailMessageFactory();

    @Test
    void criarEmailComSucesso() {
        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();
        SolicitacaoCartao solicitacao =
                CartaoTestFactory.criarSolicitacaoCartaoCompleto(99L, StatusSolicitacao.PROCESSADO);

        Map<String, String> dados = new HashMap<>();
        dados.put("numeroCartao", "**** **** **** 3456");

        EmailMessageDTO dto = factory.criarEmail("cartao-criado", cartao, solicitacao, dados);

        assertThat(dto.getTipo()).isEqualTo("cartao-criado");
        assertThat(dto.getEmail()).isEqualTo(CartaoTestConstants.EMAIL_PADRAO);
        assertThat(dto.getNome()).isEqualTo(CartaoTestConstants.NOME_PADRAO);
        assertThat(dto.getDados()).isSameAs(dados);
        assertThat(dto.getDados()).containsEntry("numeroCartao", "**** **** **** 3456");
    }

    @ParameterizedTest(name = "extrairFinalCartao({0}) -> {1}")
    @CsvSource({
            "1234567890123456,3456",
            "00001234,1234",
            "1234,1234"
    })
    void extrairFinalCartao_ok(String numero, String esperado) {
        assertThat(factory.extrairFinalCartao(numero)).isEqualTo(esperado);
    }

    @Test
    void formatarData_ok() {
        LocalDateTime data = LocalDateTime.of(2025, 1, 2, 10, 0);
        assertThat(factory.formatarData(data)).isEqualTo("02/01/2025");
    }
}
