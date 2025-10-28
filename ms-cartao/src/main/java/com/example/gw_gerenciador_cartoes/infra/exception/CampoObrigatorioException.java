package com.example.gw_gerenciador_cartoes.infra.exception;

import java.util.List;

public class CampoObrigatorioException extends RuntimeException{

    private Long solicitacaoId;
    private List<MensagemErro> mensagensErros;

    public CampoObrigatorioException(Long solicitacaoId, List<MensagemErro> mensagensErros){
        this.solicitacaoId = solicitacaoId;
        this.mensagensErros = mensagensErros;
    }

    public Long getSolicitacaoId() {
        return solicitacaoId;
    }

    public List<MensagemErro> getMensagensErros() {
        return mensagensErros;
    }
}
