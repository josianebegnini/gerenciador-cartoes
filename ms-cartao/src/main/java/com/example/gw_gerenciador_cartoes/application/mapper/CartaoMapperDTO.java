package com.example.gw_gerenciador_cartoes.application.mapper;

import com.example.gw_gerenciador_cartoes.application.dto.CartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.application.dto.SegundaViaCartaoResponseDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cartao;
import org.springframework.stereotype.Component;

@Component
public class CartaoMapperDTO {

    public CartaoResponseDTO toCartaoResponseDTO(Cartao cartao) {
        CartaoResponseDTO dto = new CartaoResponseDTO();
        dto.setId(cartao.getId());
        dto.setClienteId(cartao.getClienteId());
        dto.setContaId(cartao.getContaId());
        dto.setNumero(cartao.getNumero().substring(cartao.getNumero().length() - 4));
        dto.setCvv(cartao.getCvv());
        dto.setStatus(cartao.getStatus());
        dto.setTipoCartao(cartao.getTipoCartao());
        dto.setCategoriaCartao(cartao.getCategoriaCartao());
        dto.setDataVencimento(cartao.getDataVencimento());
        dto.setMotivoSegundaVia(cartao.getMotivoSegundaVia());
        return dto;
    }

    public SegundaViaCartaoResponseDTO toSegundaViaResponseDTO(Cartao cartao) {
        SegundaViaCartaoResponseDTO dto = new SegundaViaCartaoResponseDTO();
        dto.setNumero(cartao.getNumero());
        dto.setCvv(cartao.getCvv());
        dto.setDataVencimento(cartao.getDataVencimento());
        dto.setStatus(cartao.getStatus());
        return dto;
    }
}