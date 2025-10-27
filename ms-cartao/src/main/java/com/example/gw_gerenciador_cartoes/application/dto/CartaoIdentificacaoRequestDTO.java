package com.example.gw_gerenciador_cartoes.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class CartaoIdentificacaoRequestDTO {
    @NotBlank(message = "O número é obrigatório")
    private String numero;

    @NotBlank(message = "O cvv é obrigatório")
    private String cvv;

    public CartaoIdentificacaoRequestDTO() {
    }

    public CartaoIdentificacaoRequestDTO(String numero, String cvv) {
        this.numero = numero;
        this.cvv = cvv;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartaoIdentificacaoRequestDTO that = (CartaoIdentificacaoRequestDTO) o;
        return Objects.equals(numero, that.numero) && Objects.equals(cvv, that.cvv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, cvv);
    }

    @Override
    public String toString() {
        return "CartaoIdentificacaoRequestDTO{" +
                "numero='" + numero + '\'' +
                ", cvv='" + cvv + '\'' +
                '}';
    }
}
