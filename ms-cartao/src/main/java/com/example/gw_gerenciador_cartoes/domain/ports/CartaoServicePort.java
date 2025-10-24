package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.application.dto.CartaoIdentificacaoRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.CartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.SegundaViaCartaoRequestDTO;
import com.example.gw_gerenciador_cartoes.application.dto.SegundaViaCartaoResponseDTO;

public interface CartaoServicePort {
    void gerar(Long clienteId);
    CartaoResponseDTO ativar(CartaoIdentificacaoRequestDTO dto);
    SegundaViaCartaoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto);
}
