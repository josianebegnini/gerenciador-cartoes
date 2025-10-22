package com.example.gw_gerenciador_cartoes.application.dto;

public record EnderecoDTO(
        String cidade,
        String bairro,
        String rua,
        String cep,
        String complemento,
        String numero
) {
}
