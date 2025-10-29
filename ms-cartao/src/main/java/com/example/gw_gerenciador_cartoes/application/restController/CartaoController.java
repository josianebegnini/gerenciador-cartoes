package com.example.gw_gerenciador_cartoes.application.restController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.CartaoIdentificacaoRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.CartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.SegundaViaCartaoRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.cartao.SegundaViaCartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.domain.ports.CartaoServicePort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cartoes")
public class CartaoController {

    private final CartaoServicePort cartaoService;

    public CartaoController(CartaoServicePort cartaoService) {
        this.cartaoService = cartaoService;
    }

    @PutMapping("/ativar")
    public ResponseEntity<CartaoResponseDTO> ativar(@Valid @RequestBody CartaoIdentificacaoRequestDTO dto) {
        CartaoResponseDTO response = cartaoService.ativar(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/bloquear")
    public ResponseEntity<CartaoResponseDTO> bloquear(@Valid @RequestBody CartaoIdentificacaoRequestDTO dto) {
        CartaoResponseDTO response = cartaoService.bloquear(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/segunda-via")
    public ResponseEntity<SegundaViaCartaoResponseDTO> solicitarSegundaVia(@Valid @RequestBody SegundaViaCartaoRequestDTO dto) {
        return ResponseEntity.ok(cartaoService.solicitarSegundaVia(dto));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<Page<CartaoResponseDTO>> buscarPorCliente(
            @PathVariable Long idCliente,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CartaoResponseDTO> cartoes = cartaoService.buscarPorCliente(idCliente, pageable);
        return ResponseEntity.ok(cartoes);
    }
}
