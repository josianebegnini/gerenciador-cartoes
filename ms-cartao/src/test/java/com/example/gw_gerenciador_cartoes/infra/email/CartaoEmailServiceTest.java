package com.example.gw_gerenciador_cartoes.infra.email;

import com.example.gw_gerenciador_cartoes.application.dto.email.EmailMessageDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusSolicitacao;
import com.example.gw_gerenciador_cartoes.infra.messaging.EmailPublisher;
import com.example.gw_gerenciador_cartoes.service.SolicitacaoCartaoService;
import com.example.gw_gerenciador_cartoes.testutil.CartaoTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartaoEmailServiceTest {

    @Mock
    private EmailPublisher emailPublisher;

    @Mock
    private SolicitacaoCartaoService solicitacaoCartaoService;

    @Mock
    private EmailMessageFactory emailMessageFactory;

    @Mock
    private EmailMessageDTO emailMessageDTO;

    private CartaoEmailService service;

    @BeforeEach
    void setUp() {
        service = new CartaoEmailService(emailPublisher, solicitacaoCartaoService, emailMessageFactory);
    }

    @Test
    void enviarEmailCartaoCriadoComSucesso() {
        Contexto ctx = arrangeElegivel();
        stubFactoryComData();

        service.enviarEmailCartaoCriado(ctx.cartao());

        Map<String, String> dados = verifyCriarEmailECapturarDados("cartao-criado", ctx.cartao(), ctx.solicitacao());
        assertThat(dados.get("numeroCartao")).isEqualTo("**** **** **** 3456");
        assertThat(dados.get("dataEmissao")).isEqualTo("01/01/2025");
        verify(emailPublisher).enviarEmailNormal(emailMessageDTO);
    }

    @Test
    void enviarEmailCartaoAtivadoComSucesso() {
        Contexto ctx = arrangeElegivel();
        stubFactoryFinalENovaMensagem();

        service.enviarEmailCartaoAtivado(ctx.cartao());

        Map<String, String> dados = verifyCriarEmailECapturarDados("cartao-ativo", ctx.cartao(), ctx.solicitacao());
        assertThat(dados).containsEntry("finalCartao", "3456");
        verify(emailPublisher).enviarEmailNormal(emailMessageDTO);
    }

    @Test
    void enviarEmailCartaoBloqueado() {
        Contexto ctx = arrangeElegivel();
        stubFactoryComData();

        service.enviarEmailCartaoBloqueado(ctx.cartao(), "teste-motivo");

        Map<String, String> dados = verifyCriarEmailECapturarDados("cartao-bloqueado", ctx.cartao(), ctx.solicitacao());
        assertThat(dados.get("finalCartao")).isEqualTo("3456");
        assertThat(dados.get("dataBloqueio")).isEqualTo("01/01/2025");
        verify(emailPublisher).enviarEmailAltaPrioridade(emailMessageDTO);
    }

    @Test
    void enviarEmailSegundaVia() {
        Contexto ctx = arrangeElegivel();
        stubFactoryComData();

        service.enviarEmailSegundaVia(ctx.cartao());

        Map<String, String> dados = verifyCriarEmailECapturarDados("segunda-via", ctx.cartao(), ctx.solicitacao());
        assertThat(dados.get("finalCartao")).isEqualTo("3456");
        assertThat(dados.get("dataEmissao")).isEqualTo("01/01/2025");
        assertThat(dados.get("previsaoEntrega")).isEqualTo("7 dias úteis");
        verify(emailPublisher).enviarEmailNormal(emailMessageDTO);
    }

    @ParameterizedTest(name = "solicitacaoId={0} → nenhum envio e zero interações")
    @NullSource
    @ValueSource(longs = 0L)
    void quandoSolicitacaoIdInvalido_naoInterageComDependencias(Long solicitacaoId) {
        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();
        cartao.setSolicitacaoId(solicitacaoId);

        service.enviarEmailCartaoCriado(cartao);
        service.enviarEmailCartaoAtivado(cartao);
        service.enviarEmailCartaoBloqueado(cartao, "motivo");
        service.enviarEmailSegundaVia(cartao);

        verifyNoInteractions(solicitacaoCartaoService, emailPublisher, emailMessageFactory);
    }

    private record Contexto(Cartao cartao, SolicitacaoCartao solicitacao) {}

    private Contexto arrangeElegivel() {
        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();
        SolicitacaoCartao solicitacao = CartaoTestFactory.criarSolicitacaoCartaoCompleto(99L, StatusSolicitacao.PROCESSADO);
        when(solicitacaoCartaoService.buscarPorId(99L)).thenReturn(solicitacao);
        return new Contexto(cartao, solicitacao);
    }

    private void stubFactoryFinalENovaMensagem() {
        when(emailMessageFactory.extrairFinalCartao(anyString())).thenReturn("3456");
        when(emailMessageFactory.criarEmail(anyString(), any(Cartao.class), any(SolicitacaoCartao.class), anyMap()))
                .thenReturn(emailMessageDTO);
    }

    private void stubFactoryComData() {
        when(emailMessageFactory.extrairFinalCartao(anyString())).thenReturn("3456");
        when(emailMessageFactory.formatarData(any())).thenReturn("01/01/2025");
        when(emailMessageFactory.criarEmail(anyString(), any(Cartao.class), any(SolicitacaoCartao.class), anyMap()))
                .thenReturn(emailMessageDTO);
    }

    private Map<String, String> verifyCriarEmailECapturarDados(String template, Cartao cartao, SolicitacaoCartao solicitacao) {
        ArgumentCaptor<Map<String, String>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailMessageFactory).criarEmail(eq(template), eq(cartao), eq(solicitacao), mapCaptor.capture());
        return mapCaptor.getValue();
    }
}
