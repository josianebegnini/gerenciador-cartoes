package com.example.gw_gerenciador_cartoes.service.service.validator;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import com.example.gw_gerenciador_cartoes.service.validator.CartaoStatusValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CartaoStatusValidatorTest {

    private CartaoStatusValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CartaoStatusValidator();
    }

    @Test
    void deveAtivarCartaoComStatusDesativado() {
        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.DESATIVADO);

        assertDoesNotThrow(() -> validator.validarAlteracaoStatus(cartao, StatusCartao.ATIVADO));
        assertEquals(StatusCartao.ATIVADO, cartao.getStatus());
    }

    @Test
    void deveLancarExcecaoAoAtivarCartaoComStatusDiferenteDeDesativado() {
        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.ATIVADO);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                validator.validarAlteracaoStatus(cartao, StatusCartao.ATIVADO));

        assertEquals(MensagensErroConstantes.CARTAO_ATIVACAO_STATUS_INVALIDO, ex.getMessage());
    }

    @Test
    void deveBloquearCartaoComStatusAtivado() {
        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.ATIVADO);

        assertDoesNotThrow(() -> validator.validarAlteracaoStatus(cartao, StatusCartao.BLOQUEADO));
        assertEquals(StatusCartao.BLOQUEADO, cartao.getStatus());
    }

    @Test
    void deveLancarExcecaoAoBloquearCartaoComStatusDiferenteDeAtivado() {
        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.DESATIVADO);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                validator.validarAlteracaoStatus(cartao, StatusCartao.BLOQUEADO));

        assertEquals(MensagensErroConstantes.CARTAO_BLOQUEAR_STATUS_INVALIDO, ex.getMessage());
    }

    @Test
    void deveLancarExcecaoParaStatusNaoSuportado() {
        Cartao cartao = new Cartao();
        cartao.setStatus(StatusCartao.ATIVADO);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                validator.validarAlteracaoStatus(cartao, StatusCartao.CANCELADO));

        assertEquals(MensagensErroConstantes.CARTAO_STATUS_NÃO_SUPORTADO, ex.getMessage());
    }

}