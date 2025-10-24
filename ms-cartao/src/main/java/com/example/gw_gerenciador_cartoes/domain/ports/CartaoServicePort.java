package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.application.dto.*;

public interface CartaoServicePort {
    void gerar(CriarCartaoMessageDTO dto);
    CartaoResponseDTO ativar(CartaoIdentificacaoRequestDTO dto);
    SegundaViaCartaoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto);
}
