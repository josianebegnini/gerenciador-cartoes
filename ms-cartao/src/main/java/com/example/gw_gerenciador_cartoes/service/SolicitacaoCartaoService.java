package com.example.gw_gerenciador_cartoes.service;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusSolicitacao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;
import com.example.gw_gerenciador_cartoes.domain.ports.SolicitacaoCartaoRepositoryPort;
import com.example.gw_gerenciador_cartoes.domain.ports.SolicitacaoCartaoServicePort;
import com.example.gw_gerenciador_cartoes.infra.exception.MensagensErroConstantes;
import com.example.gw_gerenciador_cartoes.infra.exception.RegraNegocioException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SolicitacaoCartaoService implements SolicitacaoCartaoServicePort {

    private final SolicitacaoCartaoRepositoryPort solicitacaoCartaoRepository;

    public SolicitacaoCartaoService(SolicitacaoCartaoRepositoryPort solicitacaoCartaoRepository) {
        this.solicitacaoCartaoRepository = solicitacaoCartaoRepository;
    }

    @Override
    public SolicitacaoCartao salvar(Long clienteId, Long contaId, TipoCartao tipoCartao, TipoEmissao tipoEmissao, String nome) {
        SolicitacaoCartao solicitacao = new SolicitacaoCartao();
        solicitacao.setClienteId(clienteId);
        solicitacao.setContaId(contaId);
        solicitacao.setNome(nome);
        solicitacao.setStatus(StatusSolicitacao.EM_ANDAMENTO);
        solicitacao.setTipoCartao(tipoCartao);
        solicitacao.setTipoEmissao(tipoEmissao);
        solicitacao.setDataSolicitacao(LocalDateTime.now());
        solicitacao.setUltimaDataProcessamento(LocalDateTime.now());
        return solicitacaoCartaoRepository.salvar(solicitacao)
                .orElseThrow(() -> new RegraNegocioException(MensagensErroConstantes.ERRO_PERSISTENCIA));
    }

    @Override
    public void rejeitarSolicitacao(Long solicitacaoId, String motivo, String mensagemSolicitacao) {
        SolicitacaoCartao solicitacao = solicitacaoCartaoRepository.buscarPorId(solicitacaoId)
                .orElseThrow(() -> new RegraNegocioException(MensagensErroConstantes.SOLICITACAO_NAO_ENCONTRADA_REJEITAR));

        solicitacao.setStatus(StatusSolicitacao.REJEITADO);
        solicitacao.setMotivoRejeicao(motivo);
        solicitacao.setUltimaDataProcessamento(LocalDateTime.now());
        solicitacao.setMensagemSolicitacao(mensagemSolicitacao);
        solicitacaoCartaoRepository.alterar(solicitacao);
    }

    @Override
    public void finalizarComoProcessada(Long solicitacaoId, Long cartaoId) {
        SolicitacaoCartao solicitacao = solicitacaoCartaoRepository.buscarPorId(solicitacaoId)
                .orElseThrow(() -> new RegraNegocioException(MensagensErroConstantes.SOLICITACAO_NAO_ENCONTRADA_FINALIZAR));

        solicitacao.setStatus(StatusSolicitacao.PROCESSADO);
        solicitacao.setCartaoId(cartaoId);
        solicitacao.setUltimaDataProcessamento(LocalDateTime.now());
        solicitacaoCartaoRepository.alterar(solicitacao);
    }
}
