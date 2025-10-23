package com.example.gw_gerenciador_cartoes.application.restController;

import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;
import com.example.gw_gerenciador_cartoes.application.dto.SegundaViaCartaoRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.CartaoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    private final CartaoServicePort cartaoService;

    public CartaoController(CartaoServicePort cartaoService) {
        this.cartaoService = cartaoService;
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<CartaoResponseDTO> ativar(@PathVariable Long id) {
        CartaoResponseDTO response = cartaoService.ativar(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/segunda-via")
    public ResponseEntity<CartaoResponseDTO> solicitarSegundaVia(@Valid @RequestBody SegundaViaCartaoRequestDTO dto) {
        return ResponseEntity.ok(cartaoService.solicitarSegundaVia(dto.getIdCartaoOriginal(), dto.getMotivo()));
    }

}
