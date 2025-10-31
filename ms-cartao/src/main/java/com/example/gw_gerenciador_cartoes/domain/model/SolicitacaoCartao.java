package com.example.gw_gerenciador_cartoes.domain.model;

import com.example.gw_gerenciador_cartoes.infra.enums.StatusSolicitacao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.infra.enums.TipoEmissao;

import java.time.LocalDateTime;

public class SolicitacaoCartao extends AbstractCartao{

    private Long cartaoId;
    private String nome;
    private String email;
    private StatusSolicitacao status;
    private String motivoRejeicao;
    private TipoCartao tipoCartao;
    private TipoEmissao tipoEmissao;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime ultimaDataProcessamento;
    private String mensagemSolicitacao;

    public SolicitacaoCartao() {
    }

    public SolicitacaoCartao(Long cartaoId, String nome, String email, StatusSolicitacao status, String motivoRejeicao, TipoCartao tipoCartao, TipoEmissao tipoEmissao, LocalDateTime dataSolicitacao, LocalDateTime ultimaDataProcessamento, String mensagemSolicitacao) {
        this.cartaoId = cartaoId;
        this.nome = nome;
        this.email = email;
        this.status = status;
        this.motivoRejeicao = motivoRejeicao;
        this.tipoCartao = tipoCartao;
        this.tipoEmissao = tipoEmissao;
        this.dataSolicitacao = dataSolicitacao;
        this.ultimaDataProcessamento = ultimaDataProcessamento;
        this.mensagemSolicitacao = mensagemSolicitacao;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public TipoCartao getTipoCartao() {
        return tipoCartao;
    }

    public void setTipoCartao(TipoCartao tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    public TipoEmissao getTipoEmissao() {
        return tipoEmissao;
    }

    public void setTipoEmissao(TipoEmissao tipoEmissao) {
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
