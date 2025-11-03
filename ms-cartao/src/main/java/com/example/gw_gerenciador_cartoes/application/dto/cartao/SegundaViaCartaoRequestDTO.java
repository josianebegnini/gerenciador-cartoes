package com.example.gw_gerenciador_cartoes.application.dto.cartao;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class SegundaViaCartaoRequestDTO {

    @NotBlank(message = "O número é obrigatório")
    private String numero;

    @NotBlank(message = "O cvv é obrigatório")
    private String cvv;

    @NotBlank(message = "O motivo é obrigatório")
    private String motivo;

    public SegundaViaCartaoRequestDTO() {
    }

    public SegundaViaCartaoRequestDTO(String numero, String cvv, String motivo) {
        this.numero = numero;
        this.cvv = cvv;
        this.motivo = motivo;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SegundaViaCartaoRequestDTO that = (SegundaViaCartaoRequestDTO) o;
        return Objects.equals(numero, that.numero) && Objects.equals(cvv, that.cvv) && Objects.equals(motivo, that.motivo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, cvv, motivo);
    }

    @Override
    public String toString() {
        return "SegundaViaCartaoRequestDTO{" +
                "numero='" + numero + '\'' +
                ", cvv='" + cvv + '\'' +
                ", motivoSegundaVia='" + motivo + '\'' +
                '}';
    }
}
