package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.application.dto.cartao.*;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartaoServicePort {
    void processarSolicitacao(CriarCartaoMessageDTO dto);
    CartaoResponseDTO ativar(CartaoIdentificacaoRequestDTO dto);
    CartaoResponseDTO bloquear(CartaoIdentificacaoRequestDTO dto);
    SegundaViaCartaoResponseDTO solicitarSegundaVia(SegundaViaCartaoRequestDTO dto);
    Cartao buscarCartaoPorNumeroECvv(String numero, String cvv);
    Page<CartaoResponseDTO> buscarPorCliente(Long idCliente, Pageable pageable);

}
