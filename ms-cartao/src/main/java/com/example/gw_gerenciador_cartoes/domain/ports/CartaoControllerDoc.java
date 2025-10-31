package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@Tag(name = "Cartões", description = "Operações relacionadas aos cartões dos clientes")
public interface CartaoControllerDoc {

    @Operation(summary = "Alterar status do cartão", description = "Permite alterar o status de um cartão existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status alterado com sucesso",
                content = @Content(schema = @Schema(implementation = CartaoClienteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    ResponseEntity<CartaoInfoResponseDTO> alterarStatus(@RequestBody @Valid AlterarStatusRequestDTO dto);

    @Operation(summary = "Solicitar segunda via do cartão", description = "Gera uma nova via do cartão com nova numeração e CVV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Segunda via gerada com sucesso",
                    content = @Content(schema = @Schema(implementation = CartaoInfoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado")
    })
    ResponseEntity<CartaoInfoResponseDTO> solicitarSegundaVia(@RequestBody @Valid SegundaViaCartaoRequestDTO dto);

    @Operation(summary = "Buscar cartões por cliente", description = "Retorna os cartões associados a um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartões encontrados",
                    content = @Content(schema = @Schema(implementation = CartaoClienteResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    ResponseEntity<Page<CartaoClienteResponseDTO>> buscarPorCliente(@PathVariable Long idCliente, Pageable pageable);

    @Operation(summary = "Cadastrar cartão existente", description = "Permite cadastrar um cartão já existente no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cartão cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = CartaoClienteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    ResponseEntity<CartaoClienteResponseDTO> cadastrarCartaoExistente(@RequestBody @Valid CadastrarCartaoExistenteRequestDTO dto);

    @Operation(summary = "Listar todos os cartões", description = "Retorna todos os cartões cadastrados no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartões listados com sucesso",
                    content = @Content(schema = @Schema(implementation = CartaoResponseDTO.class)))
    })
    ResponseEntity<Page<CartaoResponseDTO>> listarTodos(Pageable pageable);

}
