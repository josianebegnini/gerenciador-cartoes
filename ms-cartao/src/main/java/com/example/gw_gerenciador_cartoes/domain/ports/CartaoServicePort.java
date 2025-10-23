package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.application.dto.CartaoResponseDTO;

public interface CartaoServicePort {
    void gerar(Long clienteId);
    CartaoResponseDTO ativar(Long idCartao);
    CartaoResponseDTO solicitarSegundaVia(Long idCartaoOriginal, String motivo);
}
