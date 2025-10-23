package com.example.gw_gerenciador_cartoes.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SegundaViaCartaoRequestDTO {
    @NotNull(message = "O ID do cartão original é obrigatório")
    private Long idCartaoOriginal;

    @NotBlank(message = "O motivo é obrigatório")
    private String motivo;

    public SegundaViaCartaoRequestDTO() {
    }

    public SegundaViaCartaoRequestDTO(Long idCartaoOriginal, String motivo) {
        this.idCartaoOriginal = idCartaoOriginal;
        this.motivo = motivo;
    }

    public Long getIdCartaoOriginal() {
        return idCartaoOriginal;
    }

    public void setIdCartaoOriginal(Long idCartaoOriginal) {
        this.idCartaoOriginal = idCartaoOriginal;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

}
