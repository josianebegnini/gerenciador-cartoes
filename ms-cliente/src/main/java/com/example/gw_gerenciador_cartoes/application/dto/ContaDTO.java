package com.example.gw_gerenciador_cartoes.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ContaDTO(
        @NotBlank(message = "agencia é obrigatória") String agencia,
        @NotBlank(message = "tipo é obrigatório") String tipo
) {}


