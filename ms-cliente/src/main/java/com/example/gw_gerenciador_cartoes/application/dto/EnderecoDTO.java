package com.example.gw_gerenciador_cartoes.application.dto;

import jakarta.validation.constraints.NotBlank;

public record EnderecoDTO(
        @NotBlank(message = "Cidade é obrigatória") String cidade,
        @NotBlank(message = "Bairro é obrigatório") String bairro,
        @NotBlank(message = "Rua é obrigatória") String rua,
        @NotBlank(message = "CEP é obrigatório") String cep,
        @NotBlank(message = "Complemento é obrigatório") String complemento,
        @NotBlank(message = "Número é obrigatório") String numero
) {}


