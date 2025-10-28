package com.example.gw_gerenciador_cartoes.infra.exception;

public class MensagemErro {
    private String mensagem;

    public MensagemErro(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }
}
