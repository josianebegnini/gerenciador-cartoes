package com.example.gw_gerenciador_cartoes.application.dto;

import jakarta.validation.constraints.NotBlank;

public class SegundaViaCartaoRequestDTO {

    @NotBlank(message = "O número é obrigatório")
    private String numero;

    @NotBlank(message = "O cvv é obrigatório")
    private String cvv;

    @NotBlank(message = "O motivo é obrigatório")
    private String motivoSegundaVia;

    public SegundaViaCartaoRequestDTO() {
    }

    public SegundaViaCartaoRequestDTO(String numero, String cvv, String motivo) {
        this.numero = numero;
        this.cvv = cvv;
        this.motivoSegundaVia = motivo;
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

    public String getMotivoSegundaVia() {
        return motivoSegundaVia;
    }

    public void setMotivoSegundaVia(String motivoSegundaVia) {
        this.motivoSegundaVia = motivoSegundaVia;
    }
}
