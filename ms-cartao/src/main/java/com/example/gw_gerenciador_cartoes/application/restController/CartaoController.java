package com.example.gw_gerenciador_cartoes.application.restController;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.AlterarStatusRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CadastrarCartaoExistenteRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.SegundaViaCartaoRequestDTO;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoControllerDoc;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartoes")
public class CartaoController implements CartaoControllerDoc {

    private final CartaoServicePort cartaoService;

    public CartaoController(CartaoServicePort cartaoService) {
        this.cartaoService = cartaoService;
    }

    @PutMapping("/alterar-status")
    public ResponseEntity<CartaoResponseDTO> alterarStatus(@Valid @RequestBody AlterarStatusRequestDTO dto) {
        CartaoResponseDTO response = cartaoService.alterarStatus(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/segunda-via")
    public ResponseEntity<CartaoResponseDTO> solicitarSegundaVia(@Valid @RequestBody SegundaViaCartaoRequestDTO dto) {
        return ResponseEntity.ok(cartaoService.solicitarSegundaVia(dto));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<Page<CartaoResponseDTO>> buscarPorCliente(
            @PathVariable Long idCliente,
            @ParameterObject Pageable pageable) {

        Page<CartaoResponseDTO> cartoes = cartaoService.buscarPorCliente(idCliente, pageable);
        return ResponseEntity.ok(cartoes);
    }

    @GetMapping("/por-numero-e-cv")
    public ResponseEntity<CartaoResponseDTO> buscarPorNumeroECvv(
            @RequestParam String numero,
            @RequestParam String cvv) {

        return ResponseEntity.ok(cartaoService.buscarPorNumeroECvv(numero, cvv));
    }

    @PostMapping("/cadastrar-existente")
    public ResponseEntity<CartaoResponseDTO> cadastrarCartaoExistente(
            @RequestBody @Valid CadastrarCartaoExistenteRequestDTO dto) {
        CartaoResponseDTO response = cartaoService.cadastrarCartaoExistente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<CartaoResponseDTO>> listarTodos(
            @ParameterObject Pageable pageable) {
        Page<CartaoResponseDTO> cartoes = cartaoService.listarTodos(pageable);
        return ResponseEntity.ok(cartoes);
    }


    @GetMapping("/filtro")
    public ResponseEntity<Page<CartaoResponseDTO>> buscarCartoes(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String cvv,
            @ParameterObject Pageable pageable) {

        Page<CartaoResponseDTO> cartoes = cartaoService.buscarCartoes(clienteId, numero, cvv, pageable);
        return ResponseEntity.ok(cartoes);
    }
}
