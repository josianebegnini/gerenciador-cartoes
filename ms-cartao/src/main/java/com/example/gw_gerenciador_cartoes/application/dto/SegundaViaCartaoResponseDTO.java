package com.example.gw_gerenciador_cartoes.application.dto;

import com.example.gw_gerenciador_cartoes.domain.enums.StatusCartao;

import java.time.LocalDate;
import java.util.Objects;

public class SegundaViaCartaoResponseDTO {
    private String numero;
    private String cvv;
    private LocalDate dataVencimento;
    private StatusCartao status;

    public SegundaViaCartaoResponseDTO() {
    }

    public SegundaViaCartaoResponseDTO(String numero, String cvv, LocalDate dataVencimento, StatusCartao status) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SegundaViaCartaoResponseDTO that = (SegundaViaCartaoResponseDTO) o;
        return Objects.equals(numero, that.numero) && Objects.equals(cvv, that.cvv) && Objects.equals(dataVencimento, that.dataVencimento) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, cvv, dataVencimento, status);
    }

    @Override
    public String toString() {
        return "SegundaViaCartaoResponseDTO{" +
                "numero='" + numero + '\'' +
                ", cvv='" + cvv + '\'' +
                ", dataVencimento=" + dataVencimento +
                ", status=" + status +
                '}';
    }
}
