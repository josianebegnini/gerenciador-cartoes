package com.example.gw_gerenciador_cartoes.domain.model;

import java.time.LocalDate;

public class Cartao {

    private Long clienteId;
    private String numero;
    private String cvv;
    private LocalDate dataVencimento;
    private String tipoConta;
    private String status;
    private String formatoCartao;

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

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(String tipoConta) {
        this.tipoConta = tipoConta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String isFormatoCartao() {
        return formatoCartao;
    }

    public void setFormatoCartao(String formatoCartao) {
        this.formatoCartao = formatoCartao;
    }
}
