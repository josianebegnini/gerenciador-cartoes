package com.example.gw_gerenciador_cartoes.application.dto.cartao;

import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissaoCartao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartaoResponseDTO {
    private Long id;
    private Long clienteId;
    private Long contaId;
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
    private TipoEmissaoCartao tipoEmissaoCartao;
    private BigDecimal limite;

    public CartaoResponseDTO() {
    }

    public CartaoResponseDTO(Long id, Long clienteId, Long contaId, Long solicitacaoId, String nome, String email, String numero, String cvv, LocalDateTime dataVencimento, LocalDateTime dataCriacao, StatusCartao status, String motivoStatus, TipoCartao tipoCartao, TipoEmissaoCartao tipoEmissaoCartao, BigDecimal limite) {
        this.id = id;
        this.clienteId = clienteId;
        this.contaId = contaId;
        this.solicitacaoId = solicitacaoId;
        this.nome = nome;
        this.email = email;
        this.numero = numero;
        this.cvv = cvv;
        this.dataVencimento = dataVencimento;
        this.dataCriacao = dataCriacao;
        this.status = status;
        this.motivoStatus = motivoStatus;
        this.tipoCartao = tipoCartao;
        this.tipoEmissaoCartao = tipoEmissaoCartao;
        this.limite = limite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
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

    public TipoEmissaoCartao getTipoEmissaoCartao() {
        return tipoEmissaoCartao;
    }

    public void setTipoEmissaoCartao(TipoEmissaoCartao tipoEmissaoCartao) {
        this.tipoEmissaoCartao = tipoEmissaoCartao;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }
}
