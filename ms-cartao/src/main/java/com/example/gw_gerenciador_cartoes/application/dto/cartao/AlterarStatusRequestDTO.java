package com.example.gw_gerenciador_cartoes.application.dto.cartao;

import com.example.gw_gerenciador_cartoes.infra.enums.StatusCartao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AlterarStatusRequestDTO {

    @NotBlank(message = "O número é obrigatório")
    private String numero;

    @NotBlank(message = "O cvv é obrigatório")
    private String cvv;

    @NotNull(message = "O status é obrigatório")
    private StatusCartao novoStatus;

    public AlterarStatusRequestDTO() {
    }

    public AlterarStatusRequestDTO(StatusCartao novoStatus, String cvv, String numero) {
        this.novoStatus = novoStatus;
        this.cvv = cvv;
        this.numero = numero;
    }

    public StatusCartao getNovoStatus() {
        return novoStatus;
    }

    public void setNovoStatus(StatusCartao novoStatus) {
        this.novoStatus = novoStatus;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}