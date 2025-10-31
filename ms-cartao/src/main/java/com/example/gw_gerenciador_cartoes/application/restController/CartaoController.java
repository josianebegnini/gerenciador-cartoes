package com.example.gw_gerenciador_cartoes.application.restController;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.AlterarStatusRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CadastrarCartaoExistenteRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CartaoClienteResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.SegundaViaCartaoRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.SegundaViaCartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoControllerDoc;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cartoes")
public class CartaoController implements CartaoControllerDoc {

    private final CartaoServicePort cartaoService;

    public CartaoController(CartaoServicePort cartaoService) {
        this.cartaoService = cartaoService;
    }

    @PutMapping("/alterar-status")
    public ResponseEntity<CartaoClienteResponseDTO> alterarStatus(@Valid @RequestBody AlterarStatusRequestDTO dto) {
        CartaoClienteResponseDTO response = cartaoService.alterarStatus(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/segunda-via")
    public ResponseEntity<SegundaViaCartaoResponseDTO> solicitarSegundaVia(@Valid @RequestBody SegundaViaCartaoRequestDTO dto) {
        return ResponseEntity.ok(cartaoService.solicitarSegundaVia(dto));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<Page<CartaoClienteResponseDTO>> buscarPorCliente(
            @PathVariable Long idCliente,
            @ParameterObject Pageable pageable) {

        Page<CartaoClienteResponseDTO> cartoes = cartaoService.buscarPorCliente(idCliente, pageable);
        return ResponseEntity.ok(cartoes);
    }

    @PostMapping("/cadastrar-existente")
    public ResponseEntity<CartaoClienteResponseDTO> cadastrarCartaoExistente(
            @RequestBody @Valid CadastrarCartaoExistenteRequestDTO dto) {
        CartaoClienteResponseDTO response = cartaoService.cadastrarCartaoExistente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CartaoResponseDTO>> listarTodos(
            @ParameterObject Pageable pageable) {
        Page<CartaoResponseDTO> cartoes = cartaoService.listarTodos(pageable);
        return ResponseEntity.ok(cartoes);
    }

}
