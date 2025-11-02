package com.example.gw_gerenciador_cartoes.service.service;

import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.domain.ports.SolicitacaoCartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.infra.enums.StatusSolicitacao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoEmissao;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import com.example.gw_gerenciador_cartoes.service.SolicitacaoCartaoService;
import com.example.gw_gerenciador_cartoes.service.testutil.CartaoTestConstants;
import com.example.gw_gerenciador_cartoes.service.testutil.CartaoTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SolicitacaoCartaoServiceTest {

    @Mock
    private SolicitacaoCartaoRepositoryPort solicitacaoCartaoRepository;

    @InjectMocks
    private SolicitacaoCartaoService solicitacaoCartaoService;

    @Test
    void deveSalvarSolicitacaoComSucesso() {
        SolicitacaoCartao solicitacaoMock = CartaoTestFactory.criarSolicitacaoCartaoCompleto(1L, StatusSolicitacao.EM_ANDAMENTO);

        when(solicitacaoCartaoRepository.salvar(any(SolicitacaoCartao.class)))
                .thenReturn(Optional.of(solicitacaoMock));

        SolicitacaoCartao resultado = solicitacaoCartaoService.salvar(
                1L, 2L, solicitacaoMock.getTipoCartao(), solicitacaoMock.getTipoEmissao(),
                solicitacaoMock.getNome(), solicitacaoMock.getEmail()
        );

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(StatusSolicitacao.EM_ANDAMENTO, resultado.getStatus());
        verify(solicitacaoCartaoRepository).salvar(any(SolicitacaoCartao.class));
    }

    @Test
    void deveLancarExcecaoAoSalvarSolicitacaoQuandoPersistenciaFalhar() {
        when(solicitacaoCartaoRepository.salvar(any(SolicitacaoCartao.class)))
                .thenReturn(Optional.empty());

        assertThrows(RegraNegocioException.class, () -> {
            solicitacaoCartaoService.salvar(
                    1L, 2L, TipoCartao.DEBITO, TipoEmissao.FISICO, CartaoTestConstants.NOME_PADRAO, CartaoTestConstants.EMAIL_PADRAO
            );
        });
    }

    @Test
    void deveRejeitarSolicitacaoComSucesso() {

        SolicitacaoCartao solicitacao = CartaoTestFactory.criarSolicitacaoCartaoCompleto(1L, StatusSolicitacao.EM_ANDAMENTO);

        when(solicitacaoCartaoRepository.buscarPorId(1L)).thenReturn(Optional.of(solicitacao));

        solicitacaoCartaoService.rejeitarSolicitacao(1L, CartaoTestConstants.MOTIVO_DADOS_INVALIDOS, CartaoTestConstants.MENSAGEM_SOLICITACAO_REJEITADA);

        assertEquals(StatusSolicitacao.REJEITADO, solicitacao.getStatus());
        assertEquals(CartaoTestConstants.MOTIVO_DADOS_INVALIDOS, solicitacao.getMotivoRejeicao());
        assertEquals(CartaoTestConstants.MENSAGEM_SOLICITACAO_REJEITADA, solicitacao.getMensagemSolicitacao());
        verify(solicitacaoCartaoRepository).alterar(solicitacao);
    }

    @Test
    void deveLancarExcecaoAoRejeitarSolicitacaoInexistente() {
        Long solicitacaoId = 99L;

        when(solicitacaoCartaoRepository.buscarPorId(solicitacaoId)).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            solicitacaoCartaoService.rejeitarSolicitacao(solicitacaoId, CartaoTestConstants.MOTIVO_DADOS_INVALIDOS, CartaoTestConstants.MENSAGEM_SOLICITACAO_REJEITADA);
        });

        assertEquals(MensagensErroConstantes.SOLICITACAO_NAO_ENCONTRADA, exception.getMessage());

        verify(solicitacaoCartaoRepository, never()).alterar(any());
    }

    @Test
    void deveFinalizarSolicitacaoComoProcessadaComSucesso() {
        SolicitacaoCartao solicitacao = CartaoTestFactory.criarSolicitacaoCartaoCompleto(1L, StatusSolicitacao.EM_ANDAMENTO);

        when(solicitacaoCartaoRepository.buscarPorId(1L)).thenReturn(Optional.of(solicitacao));

        solicitacaoCartaoService.finalizarComoProcessada(1L, 99L);

        assertEquals(StatusSolicitacao.PROCESSADO, solicitacao.getStatus());
        assertEquals(99L, solicitacao.getCartaoId());
        verify(solicitacaoCartaoRepository).alterar(solicitacao);
    }

    @Test
    void deveLancarExcecaoAoFinalizarSolicitacaoInexistenteComoProcessada() {
        Long solicitacaoId = 99L;
        Long cartaoId = 123L;

        when(solicitacaoCartaoRepository.buscarPorId(solicitacaoId)).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            solicitacaoCartaoService.finalizarComoProcessada(solicitacaoId, cartaoId);
        });

        assertEquals(MensagensErroConstantes.SOLICITACAO_NAO_ENCONTRADA, exception.getMessage());

        verify(solicitacaoCartaoRepository, never()).alterar(any());
    }

    @Test
    void deveBuscarSolicitacaoPorIdComSucesso() {
        Long solicitacaoId = 1L;

        SolicitacaoCartao solicitacao = CartaoTestFactory.criarSolicitacaoCartaoCompleto(solicitacaoId, StatusSolicitacao.EM_ANDAMENTO);

        when(solicitacaoCartaoRepository.buscarPorId(solicitacaoId)).thenReturn(Optional.of(solicitacao));

        SolicitacaoCartao resultado = solicitacaoCartaoService.buscarPorId(solicitacaoId);

        assertNotNull(resultado);
        assertEquals(solicitacaoId, resultado.getId());
        verify(solicitacaoCartaoRepository).buscarPorId(solicitacaoId);
    }

    @Test
    void deveLancarExcecaoAoBuscarSolicitacaoPorIdInexistente() {
        Long solicitacaoId = 99L;

        when(solicitacaoCartaoRepository.buscarPorId(solicitacaoId)).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            solicitacaoCartaoService.buscarPorId(solicitacaoId);
        });

        assertEquals(MensagensErroConstantes.SOLICITACAO_NAO_ENCONTRADA, exception.getMessage());
        verify(solicitacaoCartaoRepository).buscarPorId(solicitacaoId);
    }

}
