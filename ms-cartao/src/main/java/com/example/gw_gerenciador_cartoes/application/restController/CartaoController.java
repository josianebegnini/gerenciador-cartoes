package com.example.gw_gerenciador_cartoes.application.restController;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartoes")
public class CartaoController {

    private final CartaoServicePort cartaoService;

    public CartaoController(CartaoServicePort cartaoService) {
        this.cartaoService = cartaoService;
    }

    @PutMapping("/ativar")
    public ResponseEntity<CartaoResponseDTO> ativar(@Valid @RequestBody CartaoRequestDTO dto) {
        CartaoResponseDTO response = cartaoService.ativar(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/bloquear")
    public ResponseEntity<CartaoResponseDTO> bloquear(@Valid @RequestBody CartaoRequestDTO dto) {
        CartaoResponseDTO response = cartaoService.bloquear(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/segunda-via")
    public ResponseEntity<SegundaViaCartaoResponseDTO> solicitarSegundaVia(@Valid @RequestBody SegundaViaCartaoRequestDTO dto) {
        return ResponseEntity.ok(cartaoService.solicitarSegundaVia(dto));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<Page<CartaoClienteResponseDTO>> buscarPorCliente(
            @PathVariable Long idCliente,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CartaoClienteResponseDTO> cartoes = cartaoService.buscarPorCliente(idCliente, pageable);
        return ResponseEntity.ok(cartoes);
    }
}
