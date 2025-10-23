package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.application.dto.SegundaViaCartaoResponseDTO;

public interface CartaoServicePort {
    void gerarCartao(Long clienteId);
    SegundaViaCartaoResponseDTO solicitarSegundaVia(Long idCartaoOriginal, String motivo);
}
