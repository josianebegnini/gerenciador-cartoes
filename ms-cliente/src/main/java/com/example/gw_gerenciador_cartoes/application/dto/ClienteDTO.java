package com.example.gw_gerenciador_cartoes.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record ClienteDTO(
        Long id,

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Data de nascimento é obrigatória")
        String dataNasc,

        @NotBlank(message = "cpf é obrigatório")
        @Pattern(regexp = "\\d{11}",
                message = "cpf deve conter exatamente 11 dígitos numéricos" )
        String cpf,

        @NotNull(message = "Endereço é obrigatório")
        @Valid
        EnderecoDTO enderecoDTO,

        @NotNull(message = "Conta é obrigatório")
        @Valid
        ContaDTO contaDTO
)
 {

    public EnderecoDTO getEnderecoDTO() {
        return enderecoDTO;
    }

     public ContaDTO getContaDTO() {
         return contaDTO;
     }

}
