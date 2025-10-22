package com.example.gw_gerenciador_cartoes.application.dto;

public record ClienteDTO(
        Long id,
        String nome,
        String email,
        String dataNasc,
        String CPF,
        EnderecoDTO enderecoDTO) {

}
