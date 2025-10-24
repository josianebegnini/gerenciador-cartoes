package com.example.gw_gerenciador_cartoes.application.dto;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;

import java.time.LocalDate;

public class SegundaViaCartaoResponseDTO {
    private String numero;
    private String cvv;
    private LocalDate dataVencimento;
    private StatusCartao status;

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
}
