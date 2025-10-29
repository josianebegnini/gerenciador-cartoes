package com.example.gw_gerenciador_cartoes.domain.model;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusSolicitacao;

import java.time.LocalDateTime;

public class SolicitacaoCartao extends AbstractCartao{
    private Long cartaoId;
    private String nome;
    private String motivoRejeicao;
    private StatusSolicitacao status;
    private String tipoCartao;
    private String tipoEmissao;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime ultimaDataProcessamento;
    private String mensagemSolicitacao;

    public SolicitacaoCartao() {
    }

    public Long getCartaoId() {
        return cartaoId;
    }

    public void setCartaoId(Long cartaoId) {
        this.cartaoId = cartaoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public String getTipoCartao() {
        return tipoCartao;
    }

    public void setTipoCartao(String tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    public String getTipoEmissao() {
        return tipoEmissao;
    }

    public void setTipoEmissao(String tipoEmissao) {
        this.tipoEmissao = tipoEmissao;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getUltimaDataProcessamento() {
        return ultimaDataProcessamento;
    }

    public void setUltimaDataProcessamento(LocalDateTime ultimaDataProcessamento) {
        this.ultimaDataProcessamento = ultimaDataProcessamento;
    }

    public String getMensagemSolicitacao() {
        return mensagemSolicitacao;
    }

    public void setMensagemSolicitacao(String mensagemSolicitacao) {
        this.mensagemSolicitacao = mensagemSolicitacao;
    }
}
