package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.application.dto.CartaoIdentificacaoRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.CartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.SegundaViaCartaoRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.SegundaViaCartaoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartaoServicePort {
    void gerar(Long clienteId);
    CartaoResponseDTO ativar(CartaoIdentificacaoRequestDTO dto);
    CartaoResponseDTO bloquear(CartaoIdentificacaoRequestDTO dto);
    SegundaViaCartaoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto);
    Page<CartaoResponseDTO> buscarPorCliente(Long idCliente, Pageable pageable);
}
