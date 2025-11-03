package com.example.gw_gerenciador_cartoes.service.service.validator;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import com.example.gw_gerenciador_cartoes.service.validator.CartaoStatusValidator;
import com.example.gw_gerenciador_cartoes.testutil.CartaoTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class CartaoStatusValidatorTest {

    private CartaoStatusValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CartaoStatusValidator();
    }

    @Test
    void deveAtivarCartaoComStatusDesativado() {
        Cartao cartao = cartaoComStatus(StatusCartao.DESATIVADO);
        LocalDateTime antiga = LocalDateTime.now().minusHours(2);
        cartao.setDataCriacao(antiga);

        LocalDateTime inicio = LocalDateTime.now();
        assertDoesNotThrow(() -> validator.validarAlteracaoStatus(cartao, StatusCartao.ATIVADO));
        LocalDateTime fim = LocalDateTime.now();

        assertEquals(StatusCartao.ATIVADO, cartao.getStatus());
        assertEquals(MensagensErroConstantes.MOTIVO_CARTAO_ATIVADO, cartao.getMotivoStatus());

        assertNotNull(cartao.getDataCriacao(), "Data de criação deve ser definida");
        assertTrue(!cartao.getDataCriacao().isBefore(inicio), "Data deve ser >= início");
        assertTrue(!cartao.getDataCriacao().isAfter(fim), "Data deve ser <= fim");
        assertTrue(cartao.getDataCriacao().isAfter(antiga), "Data deve ser > data antiga");
    }

    @ParameterizedTest(name = "ativar a partir de {0} deve falhar")
    @EnumSource(value = StatusCartao.class, names = {"ATIVADO","BLOQUEADO","CANCELADO","REJEITADO"})
    void deveFalharAoAtivarQuandoStatusNaoForDesativado(StatusCartao statusAtual) {
        Cartao cartao = cartaoComStatus(statusAtual);
        Snapshot s = snap(cartao);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> validator.validarAlteracaoStatus(cartao, StatusCartao.ATIVADO));

        assertEquals(MensagensErroConstantes.CARTAO_ATIVACAO_STATUS_INVALIDO, ex.getMessage());
        assertSemAlteracoes(cartao, s);
    }

    @Test
    void deveBloquearCartaoComStatusAtivado() {
        Cartao cartao = cartaoComStatus(StatusCartao.ATIVADO);
        Snapshot invariantes = snap(cartao);

        assertDoesNotThrow(() -> validator.validarAlteracaoStatus(cartao, StatusCartao.BLOQUEADO));

        assertEquals(StatusCartao.BLOQUEADO, cartao.getStatus());
        assertEquals(MensagensErroConstantes.MOTIVO_CARTAO_BLOQUEADO_SEGURANCA, cartao.getMotivoStatus());

        assertEquals(invariantes.dataCriacao, cartao.getDataCriacao());
        assertEquals(invariantes.solicitacaoId, cartao.getSolicitacaoId());
        assertEquals(invariantes.numero, cartao.getNumero());
    }

    @ParameterizedTest(name = "bloquear a partir de {0} deve falhar")
    @EnumSource(value = StatusCartao.class, names = {"DESATIVADO","BLOQUEADO","CANCELADO","REJEITADO"})
    void deveFalharAoBloquearQuandoStatusNaoForAtivado(StatusCartao statusAtual) {
        Cartao cartao = cartaoComStatus(statusAtual);
        Snapshot s = snap(cartao);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> validator.validarAlteracaoStatus(cartao, StatusCartao.BLOQUEADO));

        assertEquals(MensagensErroConstantes.CARTAO_BLOQUEAR_STATUS_INVALIDO, ex.getMessage());
        assertSemAlteracoes(cartao, s);
    }

    @Test
    void deveLancarExcecaoParaStatusNaoSuportado_desativadoComoDestino() {
        Cartao cartao = cartaoComStatus(StatusCartao.DESATIVADO);
        Snapshot s = snap(cartao);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> validator.validarAlteracaoStatus(cartao, StatusCartao.DESATIVADO));

        assertEquals(MensagensErroConstantes.CARTAO_STATUS_NÃO_SUPORTADO, ex.getMessage());
        assertSemAlteracoes(cartao, s);
    }

    @Test
    void deveLancarExcecaoParaStatusNaoSuportado_rejeitadoComoDestino() {
        Cartao cartao = cartaoComStatus(StatusCartao.REJEITADO);
        Snapshot s = snap(cartao);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> validator.validarAlteracaoStatus(cartao, StatusCartao.REJEITADO));

        assertEquals(MensagensErroConstantes.CARTAO_STATUS_NÃO_SUPORTADO, ex.getMessage());
        assertSemAlteracoes(cartao, s);
    }

    @Test
    void deveAtualizarDataCriacaoAoAtivarCartao() {
        Cartao cartao = cartaoComStatus(StatusCartao.DESATIVADO);
        LocalDateTime antiga = LocalDateTime.now().minusDays(1);
        cartao.setDataCriacao(antiga);

        validator.validarAlteracaoStatus(cartao, StatusCartao.ATIVADO);

        assertNotNull(cartao.getDataCriacao());
        assertTrue(cartao.getDataCriacao().isAfter(antiga));

        long diff = ChronoUnit.SECONDS.between(cartao.getDataCriacao(), LocalDateTime.now());
        assertTrue(diff <= 1, "Data de criação deve ser praticamente agora (<= 1s)");
    }

    @Test
    void deveManterDataCriacaoAoBloquearCartao() {
        Cartao cartao = cartaoComStatus(StatusCartao.ATIVADO);
        LocalDateTime original = LocalDateTime.now().minusDays(1);
        cartao.setDataCriacao(original);

        validator.validarAlteracaoStatus(cartao, StatusCartao.BLOQUEADO);

        assertEquals(original, cartao.getDataCriacao(), "Data de criação não deve mudar ao bloquear");
    }

    private static Cartao cartaoComStatus(StatusCartao status) {
        Cartao c = CartaoTestFactory.criarCartaoCompleto();
        c.setStatus(status);
        return c;
    }

    private record Snapshot(
            StatusCartao status, String motivo, LocalDateTime dataCriacao,
            Long solicitacaoId, String numero) {}

    private static Snapshot snap(Cartao c) {
        return new Snapshot(c.getStatus(), c.getMotivoStatus(), c.getDataCriacao(), c.getSolicitacaoId(), c.getNumero());
    }

    private static void assertSemAlteracoes(Cartao c, Snapshot s) {
        assertEquals(s.status, c.getStatus(), "Status não deve mudar quando exceção é lançada");
        assertEquals(s.motivo, c.getMotivoStatus(), "Motivo não deve mudar");
        assertEquals(s.dataCriacao, c.getDataCriacao(), "Data de criação não deve mudar");
        assertEquals(s.solicitacaoId, c.getSolicitacaoId(), "Solicitação não deve mudar");
        assertEquals(s.numero, c.getNumero(), "Número não deve mudar");
    }

    @Test
    void deveCancelarCartaoComStatusAtivado() {
        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();

        CartaoStatusValidator validator = new CartaoStatusValidator();
        validator.validarAlteracaoStatus(cartao, StatusCartao.CANCELADO);

        assertEquals(StatusCartao.CANCELADO, cartao.getStatus());
        assertEquals(MensagensErroConstantes.MOTIVO_CARTAO_CANCELADO, cartao.getMotivoStatus());
    }

    @Test
    void deveCancelarCartaoComStatusBloqueado() {
        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();
        cartao.setStatus(StatusCartao.BLOQUEADO);

        CartaoStatusValidator validator = new CartaoStatusValidator();
        validator.validarAlteracaoStatus(cartao, StatusCartao.CANCELADO);

        assertEquals(StatusCartao.CANCELADO, cartao.getStatus());
        assertEquals(MensagensErroConstantes.MOTIVO_CARTAO_CANCELADO, cartao.getMotivoStatus());
    }

    @Test
    void deveCancelarCartaoComStatusDesativado() {
        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();
        cartao.setStatus(StatusCartao.DESATIVADO);

        CartaoStatusValidator validator = new CartaoStatusValidator();
        validator.validarAlteracaoStatus(cartao, StatusCartao.CANCELADO);

        assertEquals(StatusCartao.CANCELADO, cartao.getStatus());
        assertEquals(MensagensErroConstantes.MOTIVO_CARTAO_CANCELADO, cartao.getMotivoStatus());
    }

    @Test
    void deveLancarExcecaoAoCancelarCartaoJaCancelado() {
        Cartao cartao = CartaoTestFactory.criarCartaoCompleto();
        cartao.setStatus(StatusCartao.CANCELADO);

        CartaoStatusValidator validator = new CartaoStatusValidator();

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () ->
                validator.validarAlteracaoStatus(cartao, StatusCartao.CANCELADO)
        );

        assertEquals(MensagensErroConstantes.CARTAO_JA_CANCELADO, exception.getMessage());
    }


}