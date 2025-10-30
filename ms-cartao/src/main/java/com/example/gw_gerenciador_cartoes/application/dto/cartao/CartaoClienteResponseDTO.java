package com.example.gw_gerenciador_cartoes.application.dto.cartao;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartaoClienteResponseDTO {

    private Long clienteId;
    private String numero;
    private String cvv;
    private LocalDateTime dataVencimento;
    private LocalDateTime dataCriacao;
    private StatusCartao status;
    private String motivoStatus;
    private TipoCartao tipoCartao;
    private TipoEmissao tipoEmissao;
    private BigDecimal limite;

    public CartaoClienteResponseDTO() {
    }

    public CartaoClienteResponseDTO(Long clienteId, String numero, String cvv, LocalDateTime dataVencimento, LocalDateTime dataCriacao, StatusCartao status, String motivoStatus, TipoCartao tipoCartao, TipoEmissao tipoEmissao, BigDecimal limite) {
        this.clienteId = clienteId;
        this.numero = numero;
        this.cvv = cvv;
        this.dataVencimento = dataVencimento;
        this.dataCriacao = dataCriacao;
        this.status = status;
        this.motivoStatus = motivoStatus;
        this.tipoCartao = tipoCartao;
        this.tipoEmissao = tipoEmissao;
        this.limite = limite;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
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
