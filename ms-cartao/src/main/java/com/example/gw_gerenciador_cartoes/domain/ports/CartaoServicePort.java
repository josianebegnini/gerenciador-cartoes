package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.application.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartaoServicePort {
    void gerar(CriarCartaoMessageDTO dto);
    CartaoResponseDTO ativar(CartaoIdentificacaoRequestDTO dto);
    CartaoResponseDTO bloquear(CartaoIdentificacaoRequestDTO dto);
    SegundaViaCartaoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto);
    Page<CartaoResponseDTO> buscarPorCliente(Long idCliente, Pageable pageable);
}
