package com.example.gw_gerenciador_cartoes.domain.model;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Cartao extends AbstractCartao{

    private Long solicitacaoId;
    private String nome;
    private String email;
    private String numero;
    private String cvv;
    private LocalDateTime dataVencimento;
    private LocalDateTime dataCriacao;
    private StatusCartao status;
    private String motivoStatus;
    private TipoCartao tipoCartao;
    private TipoEmissao tipoEmissao;
    private BigDecimal limite;

    public Cartao() {
    }

    public boolean eCredito() {
        return TipoCartao.CREDITO.equals(this.tipoCartao);
    }

    public boolean eDebito() {
        return TipoCartao.DEBITO.equals(this.tipoCartao);
    }

    public void atualizarStatus(StatusCartao novoStatus, String motivo) {
        this.status = novoStatus;
        this.motivoStatus = motivo;
    }

    public Long getSolicitacaoId() {
        return solicitacaoId;
    }

    public void setSolicitacaoId(Long solicitacaoId) {
        this.solicitacaoId = solicitacaoId;
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public LocalDateTime getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDateTime dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public StatusCartao getStatus() {
        return status;
    }

    public void setStatus(StatusCartao status) {
        this.status = status;
    }

    public String getMotivoStatus() {
        return motivoStatus;
    }

    public void setMotivoStatus(String motivoStatus) {
        this.motivoStatus = motivoStatus;
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

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }
}
