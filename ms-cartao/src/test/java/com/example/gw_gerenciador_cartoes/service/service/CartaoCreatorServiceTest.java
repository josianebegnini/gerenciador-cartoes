package com.example.gw_gerenciador_cartoes.service.service;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.ClienteContaCriadoDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.email.CartaoEmailService;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import com.example.gw_gerenciador_cartoes.service.CartaoCreatorService;
import com.example.gw_gerenciador_cartoes.service.DadosCartaoGenerator;
import com.example.gw_gerenciador_cartoes.service.validator.PoliticaExpiracaoCartao;
import com.example.gw_gerenciador_cartoes.testutil.CartaoTestConstants;
import com.example.gw_gerenciador_cartoes.testutil.CartaoTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private PoliticaExpiracaoCartao politicaExpiracaoCartao;

    @InjectMocks
    private CartaoCreatorService cartaoCreatorService;

    private static final LocalDateTime VENCIMENTO_POLITICA_FIXO =
            LocalDateTime.of(2033, 1, 15, 10, 30, 0);

    @Test
    void deveCriarCartaoComSucesso() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        Cartao cartaoSalvo = CartaoTestFactory.criarCartaoCompleto();
        Long solicitacaoId = 10L;

        String numero = CartaoTestConstants.NUMERO_PADRAO;
        String cvv = CartaoTestConstants.CVV_PADRAO;

        when(dadosCartaoGenerator.gerarNumeroCartao()).thenReturn(numero);
        when(repository.existePorNumero(anyString())).thenReturn(false);
        when(dadosCartaoGenerator.gerarCvv()).thenReturn(cvv);
        when(politicaExpiracaoCartao.calcularParaCartaoNovo()).thenReturn(VENCIMENTO_POLITICA_FIXO);
        when(repository.salvar(any(Cartao.class))).thenReturn(cartaoSalvo);

        ArgumentCaptor<Cartao> cartaoCaptor = ArgumentCaptor.forClass(Cartao.class);
        Long id = cartaoCreatorService.criarCartao(dto, solicitacaoId);

        assertEquals(100L, id);

        verify(repository).salvar(cartaoCaptor.capture());
        Cartao cartaoCriado = cartaoCaptor.getValue();

        assertEquals(solicitacaoId, cartaoCriado.getSolicitacaoId());
        assertEquals(dto.getClienteId(), cartaoCriado.getClienteId());
        assertEquals(dto.getContaId(), cartaoCriado.getContaId());
        assertEquals(numero, cartaoCriado.getNumero());
        assertEquals(cvv, cartaoCriado.getCvv());
        assertEquals(dto.getTipoCartao(), cartaoCriado.getTipoCartao());
        assertEquals(dto.getTipoEmissao(), cartaoCriado.getTipoEmissao());
        assertEquals(StatusCartao.DESATIVADO, cartaoCriado.getStatus());
        assertEquals(MensagensErroConstantes.MOTIVO_CARTAO_DESATIVADO_APOS_GERACAO, cartaoCriado.getMotivoStatus());
        assertNotNull(cartaoCriado.getDataCriacao());
        assertEquals(VENCIMENTO_POLITICA_FIXO, cartaoCriado.getDataVencimento());

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
        when(politicaExpiracaoCartao.calcularParaCartaoNovo()).thenReturn(VENCIMENTO_POLITICA_FIXO);
        when(repository.salvar(any(Cartao.class))).thenReturn(null);

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            cartaoCreatorService.criarCartao(dto, 10L);
        });

        assertEquals(MensagensErroConstantes.CARTAO_FALHA_AO_CRIAR, exception.getMessage());
        verify(cartaoEmailService, never()).enviarEmailCartaoCriado(any());
    }

    @Test
    void deveLancarExcecaoQuandoCartaoSalvoNaoTemId() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        String numero = CartaoTestConstants.NUMERO_PADRAO;
        String cvv = CartaoTestConstants.CVV_PADRAO;
        Cartao cartaoSemId = new Cartao();
        cartaoSemId.setId(null);

        when(dadosCartaoGenerator.gerarNumeroCartao()).thenReturn(numero);
        when(repository.existePorNumero(anyString())).thenReturn(false);
        when(dadosCartaoGenerator.gerarCvv()).thenReturn(cvv);
        when(politicaExpiracaoCartao.calcularParaCartaoNovo()).thenReturn(VENCIMENTO_POLITICA_FIXO);
        when(repository.salvar(any(Cartao.class))).thenReturn(cartaoSemId);

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            cartaoCreatorService.criarCartao(dto, 10L);
        });

        assertEquals(MensagensErroConstantes.CARTAO_FALHA_AO_CRIAR, exception.getMessage());
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
    void deveCriarCartaoDeCreditoComLimite() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setTipoCartao(TipoCartao.CREDITO);
        Cartao cartaoSalvo = CartaoTestFactory.criarCartaoCompleto();
        cartaoSalvo.setTipoCartao(TipoCartao.CREDITO);
        cartaoSalvo.setLimite(new BigDecimal("1000.00"));

        String numero = CartaoTestConstants.NUMERO_PADRAO;
        String cvv = CartaoTestConstants.CVV_PADRAO;

        when(dadosCartaoGenerator.gerarNumeroCartao()).thenReturn(numero);
        when(repository.existePorNumero(anyString())).thenReturn(false);
        when(dadosCartaoGenerator.gerarCvv()).thenReturn(cvv);
        when(politicaExpiracaoCartao.calcularParaCartaoNovo()).thenReturn(VENCIMENTO_POLITICA_FIXO);
        when(repository.salvar(any(Cartao.class))).thenReturn(cartaoSalvo);

        ArgumentCaptor<Cartao> cartaoCaptor = ArgumentCaptor.forClass(Cartao.class);
        cartaoCreatorService.criarCartao(dto, 10L);

        verify(repository).salvar(cartaoCaptor.capture());
        Cartao cartaoCriado = cartaoCaptor.getValue();

        assertEquals(TipoCartao.CREDITO, cartaoCriado.getTipoCartao());
        assertNotNull(cartaoCriado.getLimite());
        assertEquals(new BigDecimal("1000.00"), cartaoCriado.getLimite());
        assertEquals(VENCIMENTO_POLITICA_FIXO, cartaoCriado.getDataVencimento());
    }

    @Test
    void deveCriarCartaoDeDebitoSemLimite() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setTipoCartao(TipoCartao.DEBITO);
        Cartao cartaoSalvo = CartaoTestFactory.criarCartaoCompleto();
        cartaoSalvo.setTipoCartao(TipoCartao.DEBITO);

        String numero = CartaoTestConstants.NUMERO_PADRAO;
        String cvv = CartaoTestConstants.CVV_PADRAO;

        when(dadosCartaoGenerator.gerarNumeroCartao()).thenReturn(numero);
        when(repository.existePorNumero(anyString())).thenReturn(false);
        when(dadosCartaoGenerator.gerarCvv()).thenReturn(cvv);
        when(politicaExpiracaoCartao.calcularParaCartaoNovo()).thenReturn(VENCIMENTO_POLITICA_FIXO);
        when(repository.salvar(any(Cartao.class))).thenReturn(cartaoSalvo);

        ArgumentCaptor<Cartao> cartaoCaptor = ArgumentCaptor.forClass(Cartao.class);
        cartaoCreatorService.criarCartao(dto, 10L);

        verify(repository).salvar(cartaoCaptor.capture());
        Cartao cartaoCriado = cartaoCaptor.getValue();

        assertEquals(TipoCartao.DEBITO, cartaoCriado.getTipoCartao());
        assertNull(cartaoCriado.getLimite(), "Cartão de débito não deve ter limite");
        assertEquals(VENCIMENTO_POLITICA_FIXO, cartaoCriado.getDataVencimento());
    }

    @Test
    void deveCriarCartaoTipoContaSemLimite() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setTipoCartao(TipoCartao.CONTA);
        Cartao cartaoSalvo = CartaoTestFactory.criarCartaoCompleto();
        cartaoSalvo.setTipoCartao(TipoCartao.CONTA);

        String numero = CartaoTestConstants.NUMERO_PADRAO;
        String cvv = CartaoTestConstants.CVV_PADRAO;

        when(dadosCartaoGenerator.gerarNumeroCartao()).thenReturn(numero);
        when(repository.existePorNumero(anyString())).thenReturn(false);
        when(dadosCartaoGenerator.gerarCvv()).thenReturn(cvv);
        when(politicaExpiracaoCartao.calcularParaCartaoNovo()).thenReturn(VENCIMENTO_POLITICA_FIXO);
        when(repository.salvar(any(Cartao.class))).thenReturn(cartaoSalvo);

        ArgumentCaptor<Cartao> cartaoCaptor = ArgumentCaptor.forClass(Cartao.class);
        cartaoCreatorService.criarCartao(dto, 10L);

        verify(repository).salvar(cartaoCaptor.capture());
        Cartao cartaoCriado = cartaoCaptor.getValue();

        assertEquals(TipoCartao.CONTA, cartaoCriado.getTipoCartao());
        assertNull(cartaoCriado.getLimite(), "Cartão tipo CONTA não deve ter limite");
        assertEquals(VENCIMENTO_POLITICA_FIXO, cartaoCriado.getDataVencimento());
    }

    @Test
    void deveCriarCartaoTipoMultiploSemLimite() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        dto.setTipoCartao(TipoCartao.MULTIPLO);
        Cartao cartaoSalvo = CartaoTestFactory.criarCartaoCompleto();
        cartaoSalvo.setTipoCartao(TipoCartao.MULTIPLO);

        String numero = CartaoTestConstants.NUMERO_PADRAO;
        String cvv = CartaoTestConstants.CVV_PADRAO;

        when(dadosCartaoGenerator.gerarNumeroCartao()).thenReturn(numero);
        when(repository.existePorNumero(anyString())).thenReturn(false);
        when(dadosCartaoGenerator.gerarCvv()).thenReturn(cvv);
        when(politicaExpiracaoCartao.calcularParaCartaoNovo()).thenReturn(VENCIMENTO_POLITICA_FIXO);
        when(repository.salvar(any(Cartao.class))).thenReturn(cartaoSalvo);

        ArgumentCaptor<Cartao> cartaoCaptor = ArgumentCaptor.forClass(Cartao.class);
        cartaoCreatorService.criarCartao(dto, 10L);

        verify(repository).salvar(cartaoCaptor.capture());
        Cartao cartaoCriado = cartaoCaptor.getValue();

        assertEquals(TipoCartao.MULTIPLO, cartaoCriado.getTipoCartao());
        assertNull(cartaoCriado.getLimite(), "Cartão tipo MULTIPLO não deve ter limite");
        assertEquals(VENCIMENTO_POLITICA_FIXO, cartaoCriado.getDataVencimento());
    }

    @Test
    void deveSetarDataCriacaoAoCriarCartao() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        Cartao cartaoSalvo = CartaoTestFactory.criarCartaoCompleto();

        String numero = CartaoTestConstants.NUMERO_PADRAO;
        String cvv = CartaoTestConstants.CVV_PADRAO;
        LocalDateTime antes = LocalDateTime.now();

        when(dadosCartaoGenerator.gerarNumeroCartao()).thenReturn(numero);
        when(repository.existePorNumero(anyString())).thenReturn(false);
        when(dadosCartaoGenerator.gerarCvv()).thenReturn(cvv);
        when(politicaExpiracaoCartao.calcularParaCartaoNovo()).thenReturn(VENCIMENTO_POLITICA_FIXO);
        when(repository.salvar(any(Cartao.class))).thenReturn(cartaoSalvo);

        ArgumentCaptor<Cartao> cartaoCaptor = ArgumentCaptor.forClass(Cartao.class);
        cartaoCreatorService.criarCartao(dto, 10L);

        LocalDateTime depois = LocalDateTime.now();

        verify(repository).salvar(cartaoCaptor.capture());
        Cartao cartaoCriado = cartaoCaptor.getValue();

        assertNotNull(cartaoCriado.getDataCriacao());
        assertTrue(!cartaoCriado.getDataCriacao().isBefore(antes));
        assertTrue(!cartaoCriado.getDataCriacao().isAfter(depois));
        assertEquals(VENCIMENTO_POLITICA_FIXO, cartaoCriado.getDataVencimento());
    }

    @Test
    void deveUsarPoliticaDeExpiracaoAoDefinirVencimento() {
        ClienteContaCriadoDTO dto = CartaoTestFactory.criarClienteContaCriadoDTO();
        Cartao cartaoSalvo = CartaoTestFactory.criarCartaoCompleto();

        String numero = CartaoTestConstants.NUMERO_PADRAO;
        String cvv = CartaoTestConstants.CVV_PADRAO;

        when(dadosCartaoGenerator.gerarNumeroCartao()).thenReturn(numero);
        when(repository.existePorNumero(anyString())).thenReturn(false);
        when(dadosCartaoGenerator.gerarCvv()).thenReturn(cvv);
        when(politicaExpiracaoCartao.calcularParaCartaoNovo()).thenReturn(VENCIMENTO_POLITICA_FIXO);
        when(repository.salvar(any(Cartao.class))).thenReturn(cartaoSalvo);

        ArgumentCaptor<Cartao> cartaoCaptor = ArgumentCaptor.forClass(Cartao.class);
        cartaoCreatorService.criarCartao(dto, 10L);

        verify(repository).salvar(cartaoCaptor.capture());
        Cartao cartaoCriado = cartaoCaptor.getValue();

        assertEquals(VENCIMENTO_POLITICA_FIXO, cartaoCriado.getDataVencimento(),
                "Deve utilizar exatamente a data definida pela PoliticaExpiracaoCartao");
    }

    @Test
    void deveGerarNumeroCartaoUnicoComMultiplasTentativas() {
        String numero1 = CartaoTestConstants.NUMERO_PADRAO;
        String numero2 = "1111222233334444";
        String numero3 = "5555666677778888";
        String numero4 = "9999888877776666";

        when(dadosCartaoGenerator.gerarNumeroCartao())
                .thenReturn(numero1)
                .thenReturn(numero2)
                .thenReturn(numero3)
                .thenReturn(numero4);

        when(repository.existePorNumero(numero1)).thenReturn(true);
        when(repository.existePorNumero(numero2)).thenReturn(true);
        when(repository.existePorNumero(numero3)).thenReturn(true);
        when(repository.existePorNumero(numero4)).thenReturn(false);

        String resultado = cartaoCreatorService.gerarNumeroCartaoUnico();

        assertEquals(numero4, resultado);
        verify(dadosCartaoGenerator, times(4)).gerarNumeroCartao();
        verify(repository, times(4)).existePorNumero(anyString());
    }

}
