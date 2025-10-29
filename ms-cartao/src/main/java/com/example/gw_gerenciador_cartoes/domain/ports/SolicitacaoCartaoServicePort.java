package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;

public interface SolicitacaoCartaoServicePort {

    SolicitacaoCartao salvar(Long clienteId, Long contaId, String tipoCartao, String tipoEmissao, String nome);

    void rejeitarSolicitacao(Long solicitacaoId, String motivo, String mensagemSolicitacao);

    void finalizarComoProcessada(Long solicitacaoId, Long cartaoId);
}
