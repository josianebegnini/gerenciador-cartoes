package com.example.gw_gerenciador_cartoes.service.service;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.ClienteContaCriadoDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.email.CartaoEmailService;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import com.example.gw_gerenciador_cartoes.service.CartaoCreatorService;
import com.example.gw_gerenciador_cartoes.service.DadosCartaoGenerator;
import com.example.gw_gerenciador_cartoes.service.testutil.CartaoTestConstants;
import com.example.gw_gerenciador_cartoes.service.testutil.CartaoTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartaoCreatorServiceTest {

    @Mock
    private DadosCartaoGenerator dadosCartaoGenerator;

    @Mock
    private CartaoRepositoryPort repository;

    @Mock
    private CartaoEmailService cartaoEmailService;

    @InjectMocks
    private CartaoCreatorService cartaoCreatorService;

    @Test
    void deveCriarCartaoComSucesso() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        Cartao cartaoSalvo = CartaoTestFactory.criarCartaoCompleto();

        String numero = CartaoTestConstants.NUMERO_PADRAO;
        String cvv = CartaoTestConstants.CVV_PADRAO;

        when(dadosCartaoGenerator.gerarNumeroCartao()).thenReturn(numero);
        when(repository.existePorNumero(anyString())).thenReturn(false);
        when(dadosCartaoGenerator.gerarCvv()).thenReturn(cvv);
        when(repository.salvar(any(Cartao.class))).thenReturn(cartaoSalvo);

        Long id = cartaoCreatorService.criarCartao(dto, 10L);

        assertEquals(100L, id);
        verify(cartaoEmailService).enviarEmailCartaoCriado(cartaoSalvo);
    }

    @Test
    void deveLancarExcecaoQuandoCartaoNaoForSalvo() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        String numero = CartaoTestConstants.NUMERO_PADRAO;
        String cvv = CartaoTestConstants.CVV_PADRAO;

        when(dadosCartaoGenerator.gerarNumeroCartao()).thenReturn(numero);
        when(repository.existePorNumero(anyString())).thenReturn(false);
        when(dadosCartaoGenerator.gerarCvv()).thenReturn(cvv);
        when(repository.salvar(any(Cartao.class))).thenReturn(null);

        assertThrows(RegraNegocioException.class, () -> {
            cartaoCreatorService.criarCartao(dto, 10L);
        });

        verify(cartaoEmailService, never()).enviarEmailCartaoCriado(any());
    }

    @Test
    void deveGerarNumeroCartaoUnicoQuandoNumeroNaoExiste() {
        String numero = CartaoTestConstants.NUMERO_PADRAO;

        when(dadosCartaoGenerator.gerarNumeroCartao()).thenReturn(numero);
        when(repository.existePorNumero(numero)).thenReturn(false);

        String resultado = cartaoCreatorService.gerarNumeroCartaoUnico();

        assertEquals(numero, resultado);
    }

    @Test
    void deveTentarNovamenteQuandoNumeroJaExiste() {
        when(dadosCartaoGenerator.gerarNumeroCartao())
                .thenReturn(CartaoTestConstants.NUMERO_PADRAO)
                .thenReturn("5555666677778888");
        when(repository.existePorNumero(CartaoTestConstants.NUMERO_PADRAO)).thenReturn(true);
        when(repository.existePorNumero("5555666677778888")).thenReturn(false);

        String numero = cartaoCreatorService.gerarNumeroCartaoUnico();

        assertEquals("5555666677778888", numero);
    }

    @Test
    void deveCalcularDataVencimentoCorretamente() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime vencimento = cartaoCreatorService.calcularDataVencimentoNovoCartao();

        assertEquals(agora.getYear() + 3, vencimento.getYear());
        assertEquals(agora.getMonth(), vencimento.getMonth());
        assertEquals(agora.getDayOfMonth(), vencimento.getDayOfMonth());
    }
}
