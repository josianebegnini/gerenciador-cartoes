package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;

import java.util.Optional;

public interface SolicitacaoCartaoRepositoryPort {
    Optional<SolicitacaoCartao> salvar(SolicitacaoCartao solicitacaoCartao);
    void alterar(SolicitacaoCartao solicitacaoCartao);
    Optional<SolicitacaoCartao> buscarPorId(Long solicitacaoId);
}
