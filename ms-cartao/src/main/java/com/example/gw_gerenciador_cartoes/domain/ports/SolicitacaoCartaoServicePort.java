package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.infra.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoEmissao;
import com.example.gw_gerenciador_cartoes.domain.model.SolicitacaoCartao;

public interface SolicitacaoCartaoServicePort {

    SolicitacaoCartao salvar(Long clienteId, Long contaId, TipoCartao tipoCartao, TipoEmissao tipoEmissao, String nome, String email);

    void rejeitarSolicitacao(Long solicitacaoId, String motivo, String mensagemSolicitacao);

    void finalizarComoProcessada(Long solicitacaoId, Long cartaoId);
}
