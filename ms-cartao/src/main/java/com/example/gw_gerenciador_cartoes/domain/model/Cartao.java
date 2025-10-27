package com.example.gw_gerenciador_cartoes.domain.model;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoCartao;
import com.example.gw_gerenciador_cartoes.domain.enums.TipoEmissaoCartao;

import java.time.LocalDate;

public class Cartao {

    private Long id;
    private Long clienteId;
    private Long contaId;
    private String numero;
    private String cvv;
    private LocalDate dataVencimento;
    private StatusCartao status;
    private String motivoStatus;
    private TipoCartao tipoCartao;
    private TipoEmissaoCartao tipoEmissao;

    public Cartao() {
    }

    public Cartao(Long id, Long clienteId, Long contaId, String numero, String cvv, LocalDate dataVencimento, StatusCartao status, String motivoStatus, TipoCartao tipoCartao, TipoEmissaoCartao tipoEmissao) {
        this.id = id;
        this.clienteId = clienteId;
        this.contaId = contaId;
        this.numero = numero;
        this.cvv = cvv;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.motivoStatus = motivoStatus;
        this.tipoCartao = tipoCartao;
        this.tipoEmissao = tipoEmissao;
    }

    //TODO nao pode dar exceção na entidade basica
    public void atualizarStatus(StatusCartao novoStatus, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo do status não pode ser vazio.");
        }
        this.status = novoStatus;
        this.motivoStatus = motivo;
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

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
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

    public TipoEmissaoCartao getTipoEmissao() {
        return tipoEmissao;
    }

    public void setTipoEmissao(TipoEmissaoCartao tipoEmissao) {
        this.tipoEmissao = tipoEmissao;
    }
}
