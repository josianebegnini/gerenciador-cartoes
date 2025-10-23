package com.example.gw_gerenciador_cartoes.application.mapper;

import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import com.example.gw_gerenciador_cartoes.application.dto.CartaoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SegundaViaCartaoMapper {

    public CartaoResponseDTO toResponseDTO(Cartao cartao) {
        CartaoResponseDTO dto = new CartaoResponseDTO();
        dto.setId(cartao.getId());
        dto.setClienteId(cartao.getClienteId());
        dto.setNumero(cartao.getNumero().substring(cartao.getNumero().length() - 4));
        dto.setCvv(cartao.getCvv());
        dto.setStatus(cartao.getStatus());
        dto.setTipoCartao(cartao.getTipoCartao());
        dto.setCategoriaCartao(cartao.getCategoriaCartao());
        dto.setDataVencimento(cartao.getDataVencimento());
        dto.setMotivoSegundaVia(cartao.getMotivoSegundaVia());
        return dto;
    }

}
