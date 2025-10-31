package com.example.gw_gerenciador_cartoes.application.dto.cartao;

import com.example.gw_gerenciador_cartoes.infra.enums.StatusCartao;

import java.time.LocalDateTime;

public class CartaoInfoResponseDTO {
    private String numero;
    private String cvv;
    private LocalDateTime dataVencimento;
    private StatusCartao status;

    public CartaoInfoResponseDTO() {
    }

    public CartaoInfoResponseDTO(String numero, String cvv, LocalDateTime dataVencimento, StatusCartao status) {
        this.numero = numero;
        this.cvv = cvv;
        this.dataVencimento = dataVencimento;
        this.status = status;
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

    public StatusCartao getStatus() {
        return status;
    }

    public void setStatus(StatusCartao status) {
        this.status = status;
    }
}
