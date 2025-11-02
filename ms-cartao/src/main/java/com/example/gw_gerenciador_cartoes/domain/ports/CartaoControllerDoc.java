package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Cartões", description = "Operações relacionadas aos cartões dos clientes")
public interface CartaoControllerDoc {

    @Operation(summary = "Alterar status do cartão", description = "Permite alterar o status de um cartão existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status alterado com sucesso",
                content = @Content(schema = @Schema(implementation = CartaoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    ResponseEntity<CartaoResponseDTO> alterarStatus(@RequestBody @Valid AlterarStatusRequestDTO dto);

    @Operation(summary = "Solicitar segunda via do cartão", description = "Gera uma nova via do cartão com nova numeração e CVV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Segunda via gerada com sucesso",
                    content = @Content(schema = @Schema(implementation = CartaoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado")
    })
    ResponseEntity<CartaoResponseDTO> solicitarSegundaVia(@RequestBody @Valid SegundaViaCartaoRequestDTO dto);

    @Operation(summary = "Buscar cartões por cliente", description = "Retorna os cartões associados a um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartões encontrados",
                    content = @Content(schema = @Schema(implementation = CartaoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    ResponseEntity<Page<CartaoResponseDTO>> buscarPorCliente(@PathVariable Long idCliente, Pageable pageable);

    @Operation(summary = "Buscar cartão por número e CVV", description = "Retorna um cartão a partir do número e do CVV")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cartão encontrado",
                    content = @Content(schema = @Schema(implementation = CartaoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado")
    })
    ResponseEntity<CartaoResponseDTO> buscarPorNumeroECvv(
            @Parameter(description = "Número do cartão") @RequestParam String numero,
            @Parameter(description = "CVV do cartão") @RequestParam String cvv
    );
    @Operation(summary = "Cadastrar cartão existente", description = "Permite cadastrar um cartão já existente no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cartão cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = CartaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    ResponseEntity<CartaoResponseDTO> cadastrarCartaoExistente(@RequestBody @Valid CadastrarCartaoExistenteRequestDTO dto);

    @Operation(summary = "Listar todos os cartões", description = "Retorna todos os cartões cadastrados no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartões listados com sucesso",
                    content = @Content(schema = @Schema(implementation = CartaoResponseDTO.class)))
    })
    ResponseEntity<Page<CartaoResponseDTO>> listarTodos(Pageable pageable);

    @Operation(summary = "Buscar cartões com filtro", description = "Filtra cartões por clienteId, número e/ou CVV (todos opcionais)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CartaoResponseDTO.class))))
    })
    ResponseEntity<Page<CartaoResponseDTO>> buscarCartoes(
            @Parameter(description = "ID do cliente (opcional)") @RequestParam(required = false) Long clienteId,
            @Parameter(description = "Número do cartão (opcional)") @RequestParam(required = false) String numero,
            @Parameter(description = "CVV do cartão (opcional)") @RequestParam(required = false) String cvv,
            @ParameterObject Pageable pageable
    );
}
